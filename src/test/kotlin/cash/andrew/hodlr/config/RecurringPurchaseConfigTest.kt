package cash.andrew.hodlr.config

import cash.andrew.hodlr.config.Frequency.daily
import cash.andrew.hodlr.config.Frequency.monthly
import cash.andrew.hodlr.config.Frequency.weekly
import cash.andrew.hodlr.http.coinbase.model.FiatCurrency.USD
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.Month.FEBRUARY
import java.time.Month.JANUARY
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.stream.Stream

class RecurringPurchaseConfigTest {

    @Test
    fun `should return tomorrow at noon for daily frequency`() {
        val clock = Clock.fixed(Instant.ofEpochSecond(0), ZoneOffset.UTC)

        val classUnderTest = RecurringPurchaseConfig(
            frequency = daily,
            account = "3794eb46-bf4a-40f4-a102-06dcbf7b17bb",
            amount = 1000.toBigDecimal(),
            assetsToPurchase = mapOf(
                CryptoCurrency.LTC to 100.toBigDecimal()
            ),
            day = 2,
            clock = clock,
            currency = USD
        )

        classUnderTest.nextDepositZonedDateTime shouldBeEqualTo LocalDate.of(1970, JANUARY, 2)
            .atStartOfDay(ZoneOffset.UTC)
    }

    @ParameterizedTest(name = "{index} => frequency of {0} on day {1} when today is {2}/{3} should run on {4}/{5}")
    @MethodSource("nextDepositeDayProvider")
    fun `next deposit day`(frequency: Frequency, day: Int, now: LocalDate, expected: ZonedDateTime) {
        val clock = Clock.fixed(
            now.atStartOfDay(ZoneOffset.UTC).toInstant(),
            ZoneOffset.UTC
        )

        val classUnderTest = RecurringPurchaseConfig(
            frequency = frequency,
            account = "3794eb46-bf4a-40f4-a102-06dcbf7b17bb",
            amount = 1000.toBigDecimal(),
            assetsToPurchase = mapOf(
                CryptoCurrency.LTC to 100.toBigDecimal()
            ),
            day = day,
            clock = clock,
            currency = USD
        )

        classUnderTest.nextDepositZonedDateTime shouldBeEqualTo expected
    }

    @ParameterizedTest(name = "{index} => should throw illegal argument exception with message = {1}")
    @MethodSource("configProvider")
    fun `validate inputs`(create: () -> RecurringPurchaseConfig, message: String) {
        val exception = assertThrows<IllegalArgumentException> { create() }

        exception.message!! shouldBeEqualTo message
    }

    @Test
    fun `should get the correct number of milliseconds`() {

        val clockStart = LocalDate.of(2019, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)
        val classUnderTest = RecurringPurchaseConfig(
            frequency = daily,
            account = "3794eb46-bf4a-40f4-a102-06dcbf7b17bb",
            amount = 1000.toBigDecimal(),
            assetsToPurchase = mapOf(
                CryptoCurrency.LTC to 100.toBigDecimal()
            ),
            day = 1,
            clock = Clock.fixed(clockStart, ZoneOffset.UTC),
            currency = USD
        )

        classUnderTest.nextDepositEpochMillis shouldBeEqualTo 1546387200000 // milliseconds in a day
    }

    companion object {
        @JvmStatic
        fun nextDepositeDayProvider(): Stream<Arguments> {
            return Stream.of(
                arguments(
                    daily,
                    1,
                    LocalDate.of(1970, JANUARY, 1),
                    LocalDate.of(1970, JANUARY, 2).atStartOfDay(ZoneOffset.UTC)
                ),
                arguments(
                    weekly,
                    4,
                    LocalDate.of(1970, JANUARY, 1),
                    LocalDate.of(1970, JANUARY, 8).atStartOfDay(ZoneOffset.UTC)
                ),
                arguments(
                    weekly,
                    1,
                    LocalDate.of(1970, JANUARY, 1),
                    LocalDate.of(1970, JANUARY, 5).atStartOfDay(ZoneOffset.UTC)
                ),
                arguments(
                    weekly,
                    6,
                    LocalDate.of(1970, JANUARY, 1),
                    LocalDate.of(1970, JANUARY, 3).atStartOfDay(ZoneOffset.UTC)
                ),
                arguments(
                    monthly,
                    1,
                    LocalDate.of(1970, JANUARY, 1),
                    LocalDate.of(1970, FEBRUARY, 1).atStartOfDay(ZoneOffset.UTC)
                ),
                arguments(
                    monthly,
                    5,
                    LocalDate.of(1970, JANUARY, 4),
                    LocalDate.of(1970, JANUARY, 5).atStartOfDay(ZoneOffset.UTC)
                ),
                arguments(
                    monthly,
                    5,
                    LocalDate.of(1970, JANUARY, 10),
                    LocalDate.of(1970, FEBRUARY, 5).atStartOfDay(ZoneOffset.UTC)
                )
            )
        }

        @JvmStatic
        fun configProvider(): Stream<Arguments> {
            return Stream.of(
                arguments({
                    RecurringPurchaseConfig(
                        frequency = daily,
                        account = "1111-1111-1111-1111",
                        amount = 10.toBigDecimal(),
                        currency = USD,
                        assetsToPurchase = mapOf()
                    )
                }, "Assets to purchase has not values assigned"),
                arguments({
                    RecurringPurchaseConfig(
                        frequency = daily,
                        account = "1111-1111-1111-1111",
                        amount = 10.toBigDecimal(),
                        currency = USD,
                        assetsToPurchase = mapOf(
                            CryptoCurrency.BTC to 50.toBigDecimal(),
                            CryptoCurrency.LTC to 50.toBigDecimal()
                        )
                    )
                }, "Assets to purchase total is 100. When deposit amount is only 10"),
                arguments({
                    RecurringPurchaseConfig(
                        day = 30,
                        frequency = daily,
                        account = "1111-1111-1111-1111",
                        amount = 50.toBigDecimal(),
                        currency = USD,
                        assetsToPurchase = mapOf(
                            CryptoCurrency.BTC to 50.toBigDecimal()
                        )
                    )
                }, "Day is 30, it must be in range of 1 to 28"),
                arguments({
                    RecurringPurchaseConfig(
                        day = 0,
                        frequency = daily,
                        account = "1111-1111-1111-1111",
                        amount = 50.toBigDecimal(),
                        currency = USD,
                        assetsToPurchase = mapOf(
                            CryptoCurrency.BTC to 50.toBigDecimal()
                        )
                    )
                }, "Day is 0, it must be in range of 1 to 28"),
                arguments({
                    RecurringPurchaseConfig(
                        frequency = weekly,
                        account = "1111-1111-1111-1111",
                        amount = 1.toBigDecimal(),
                        currency = USD,
                        assetsToPurchase = mapOf(
                            CryptoCurrency.BTC to 1.toBigDecimal()
                        )
                    )
                }, "Amount is $1, deposit amount must be larger than $2")
            )
        }
    }
}
