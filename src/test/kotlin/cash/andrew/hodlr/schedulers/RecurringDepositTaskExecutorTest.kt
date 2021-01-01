package cash.andrew.hodlr.schedulers

import cash.andrew.hodlr.DO_NOTHING_LOGGER
import cash.andrew.hodlr.OutputStreamTest
import cash.andrew.hodlr.TestClock
import cash.andrew.hodlr.config.CryptoCurrency
import cash.andrew.hodlr.config.Frequency
import cash.andrew.hodlr.config.RecurringPurchaseConfig
import cash.andrew.hodlr.http.coinbase.CoinbaseRepository
import cash.andrew.hodlr.http.coinbase.DepositError
import cash.andrew.hodlr.http.coinbase.DepositResponse
import cash.andrew.hodlr.http.coinbase.DepositSuccess
import cash.andrew.hodlr.http.coinbase.model.FiatCurrency
import cash.andrew.hodlr.logging.ConsoleLogger
import cash.andrew.hodlr.stub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.io.IOException
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.Date
import java.util.LinkedList
import cash.andrew.hodlr.http.coinbase.model.DepositResponse as Deposit

@ExperimentalCoroutinesApi
class RecurringDepositTaskExecutorTest : OutputStreamTest() {

  @Test
  fun `should check clock and not run`() = runBlockingTest {
      val config = RecurringPurchaseConfig(
          frequency = Frequency.daily,
          account = "a71ec7ea-8e69-4ef5-921b-f7fda48d4864",
          currency = FiatCurrency.USD,
          amount = 100.toBigDecimal(),
          assetsToPurchase = mapOf(
              CryptoCurrency.BTC to 100.toBigDecimal()
          ),
          clock = Clock.fixed(Instant.ofEpochMilli(10000), ZoneOffset.UTC)
      )

      val coinbaseRepository = object : CoinbaseRepository by stub() {}

      val classUnderTest = RecurringDepositTaskExecutor(
          config = config,
          coinbaseRepository = coinbaseRepository,
          logger = DO_NOTHING_LOGGER,
          clock = object : TestClock() {
              override fun instant() = Instant.ofEpochMilli(10000)
          }
      )

      classUnderTest.shouldRun.shouldBeEqualTo(false)
  }

  @Test
  fun `should check clock, run, and update next time check`() = runBlockingTest {
      val config = RecurringPurchaseConfig(
          frequency = Frequency.daily,
          account = "a71ec7ea-8e69-4ef5-921b-f7fda48d4864",
          currency = FiatCurrency.USD,
          amount = 100.toBigDecimal(),
          assetsToPurchase = mapOf(
              CryptoCurrency.BTC to 100.toBigDecimal()
          ),
          clock = object : TestClock() {
              override fun instant() = Instant.ofEpochMilli(10000)
          }
      )

      val coinbaseRepository = object : CoinbaseRepository by stub() {}

      val classUnderTest = RecurringDepositTaskExecutor(
          config = config,
          coinbaseRepository = coinbaseRepository,
          logger = DO_NOTHING_LOGGER,
          clock = object : TestClock() {
              // easier to reset the clock used here
              // rather than the many times it's used in config
              val instants = LinkedList(
                  listOf(
                      Instant.ofEpochMilli(86_400_001),
                      Instant.ofEpochMilli(86_399_999)
                  )
              )

              override fun instant() = instants.remove()
          }
      )

      classUnderTest.shouldRun.shouldBeEqualTo(true)
      classUnderTest.shouldRun.shouldBeEqualTo(false)
  }

  @Test
  fun `should deposit successfully`() = runBlockingTest {
    val config = RecurringPurchaseConfig(
        frequency = Frequency.daily,
        account = "a71ec7ea-8e69-4ef5-921b-f7fda48d4864",
        currency = FiatCurrency.USD,
        amount = 100.toBigDecimal(),
        assetsToPurchase = mapOf(
            CryptoCurrency.BTC to 100.toBigDecimal()
        )
    )

    val coinbaseRepository = object : CoinbaseRepository by stub() {
        override suspend fun depositFunds(
            amount: BigDecimal,
            currency: FiatCurrency,
            paymentMethodId: String
        ): DepositResponse {
            amount shouldBeEqualTo config.amount
            currency shouldBeEqualTo config.currency
            paymentMethodId shouldBeEqualTo config.account

            return DepositSuccess(
                Deposit(
                    id = "d896b28d-3d08-4b60-9bf9-2f3c5d407331",
                    amount = "100",
                    currency = "BTC",
                    payoutAt = Date.from(Instant.ofEpochMilli(1337))
                )
            )
        }
    }

    val classUnderTest = RecurringDepositTaskExecutor(
        config = config,
        coinbaseRepository = coinbaseRepository,
        logger = ConsoleLogger(),
        clock = object : TestClock() {
          override fun instant() = Instant.ofEpochMilli(10000)
        }
    )

    classUnderTest.runTask()

    errorText shouldBeEqualTo ""
    outputText shouldBeEqualTo """
      Depositing funds
      Deposited funds
    """.trimIndent()
  }

  @Test
  fun `should deposit and handle error`() = runBlockingTest {
    val config = RecurringPurchaseConfig(
        frequency = Frequency.daily,
        account = "a71ec7ea-8e69-4ef5-921b-f7fda48d4864",
        currency = FiatCurrency.USD,
        amount = 100.toBigDecimal(),
        assetsToPurchase = mapOf(
            CryptoCurrency.BTC to 100.toBigDecimal()
        )
    )

    val coinbaseRepository = object : CoinbaseRepository by stub() {
      override suspend fun depositFunds(
          amount: BigDecimal,
          currency: FiatCurrency,
          paymentMethodId: String
      ) = DepositError(IOException("☹️"))
    }

    val classUnderTest = RecurringDepositTaskExecutor(
        config = config,
        coinbaseRepository = coinbaseRepository,
        logger = ConsoleLogger(),
        clock = object : TestClock() {
          override fun instant() = Instant.ofEpochMilli(10000)
        }
    )

    classUnderTest.runTask()

    errorText shouldBeEqualTo """
      There was an error depositing USD of 100 into a71ec7ea-8e69-4ef5-921b-f7fda48d4864
      java.io.IOException: ☹️
    """.trimIndent()
    outputText shouldBeEqualTo "Depositing funds"
  }
}
