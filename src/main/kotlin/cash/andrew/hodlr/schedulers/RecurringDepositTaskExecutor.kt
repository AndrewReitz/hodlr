package cash.andrew.hodlr.schedulers

import cash.andrew.hodlr.config.RecurringPurchaseConfig
import cash.andrew.hodlr.http.coinbase.CoinbaseRepository
import cash.andrew.hodlr.http.coinbase.DepositError
import cash.andrew.hodlr.http.coinbase.DepositSuccess
import cash.andrew.hodlr.logging.Logger
import cash.andrew.hodlr.util.exhaustive
import cash.andrew.hodlr.util.minutes
import org.jetbrains.annotations.TestOnly
import java.time.Clock
import javax.inject.Inject
import javax.inject.Singleton
import java.time.ZonedDateTime

@Singleton
class RecurringDepositTaskExecutor @Inject constructor(
    private val config: RecurringPurchaseConfig,
    private val coinbaseRepository: CoinbaseRepository,
    private val logger: Logger,
    private val clock: Clock = Clock.systemDefaultZone()
) {

  private var nextRun: Long = config.nextDepositEpochMillis

  suspend fun start() {
    while (true) {
      10.minutes().delay()

      logger.trace { "Checking shouldRun" }
      if (shouldRun) {
        logger.trace { "shouldRun was true" }
        runTask()
      }
    }
  }

  @get:TestOnly
  val shouldRun: Boolean get() = if (nextRun > clock.instant().toEpochMilli()) {
    logger.trace {
      "Checking clock: ${ZonedDateTime.now(clock)}, " +
          "next deposit is scheduled for ${config.nextDepositZonedDateTime}"
    }
    false
  } else {
    logger.trace { "Should deposit updating nextRun to ${config.nextDepositZonedDateTime}" }
    nextRun = config.nextDepositEpochMillis
    true
  }

  @TestOnly
  suspend fun runTask() {
    logger.info { "Depositing funds" }

    val (_, account, amount, currency) = config
    val result = coinbaseRepository.depositFunds(amount, currency, account)

    when (result) {
      is DepositSuccess -> logger.info { "Deposited funds" }
      is DepositError -> logger.error(result.error) {
        "There was an error depositing $currency of $amount into $account"
      }.also {
        // reset back to try again on the next when there is an error
        nextRun = ZonedDateTime.now(clock).minusDays(1).toInstant().toEpochMilli()
      }
    }.exhaustive
  }
}
