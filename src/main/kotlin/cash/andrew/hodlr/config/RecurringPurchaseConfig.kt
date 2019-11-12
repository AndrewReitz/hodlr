package cash.andrew.hodlr.config

import cash.andrew.hodlr.http.coinbase.model.FiatCurrency
import cash.andrew.hodlr.util.monthLength
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime

data class RecurringPurchaseConfig(
    val frequency: Frequency,
    val account: String,
    val amount: BigDecimal,
    val currency: FiatCurrency,
    val assetsToPurchase: Map<CryptoCurrency, BigDecimal>,
    val day: Int = 1,
    private val clock: Clock = Clock.systemDefaultZone()
) {
  init {
    require(assetsToPurchase.isNotEmpty()) { "Assets to purchase has not values assigned" }
    val total = assetsToPurchase.values.reduce { acc, bigDecimal ->
      acc + bigDecimal
    }
    require(total <= amount) { "Assets to purchase total is $total. When deposit amount is only $amount" }
    require(day in 1..28) { "Day is $day, it must be in range of 1 to 28" }
    require(amount > 2.toBigDecimal()) { "Amount is $$amount, deposit amount must be larger than $2"}
  }

  /**
   * Get the next date to load coinbase with money, where the time is set to midnight
   * at the [java.time.ZoneId] for the clock provided in the constructor.
   */
  val nextDepositZonedDateTime: ZonedDateTime
    get() {
      val now = LocalDate.now(clock)

      return when (frequency) {
        Frequency.daily -> now.plusDays(1)
        Frequency.weekly -> {
          val currentDayOfWeek = now.dayOfWeek.value
          when {
            currentDayOfWeek < day -> now.plusDays(day.toLong() - currentDayOfWeek.toLong())
            else -> now.plusDays(7.toLong() - now.dayOfWeek.value.toLong() + day)
          }
        }
        Frequency.monthly -> {
          when {
            day <= now.dayOfMonth -> now.plusMonths(1).withDayOfMonth(day)
            day > now.monthLength() -> now.withDayOfMonth(now.monthLength())
            else -> now.withDayOfMonth(day)
          }
        }
      }.atStartOfDay(clock.zone)
    }

  val nextDepositEpochMillis: Long get() = nextDepositZonedDateTime.toInstant().toEpochMilli()
}

