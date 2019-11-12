package cash.andrew.hodlr.cli

import cash.andrew.hodlr.schedulers.RecurringDepositTaskExecutor
import cash.andrew.hodlr.component
import cash.andrew.hodlr.logging.Logger
import cash.andrew.hodlr.schedulers.RecurringPurchaseTaskExecutor
import cash.andrew.hodlr.util.defaultLazy
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

class Run : CliktCommand() {
  private val logger: Logger by defaultLazy { component.logger }
  private val recurringDepositTaskExecutor: RecurringDepositTaskExecutor by defaultLazy { component.recurringDepositTaskExecutor }
  private val recurringPurchaseTaskExecutor: RecurringPurchaseTaskExecutor by defaultLazy { component.recurringPurchaseTaskExecutor }

  override fun run() = runBlocking<Unit> {
    logger.info { "Starting schedulers..." }

    scheduleShutdownHook()
    launch { recurringPurchaseTaskExecutor.start() }
    launch { recurringDepositTaskExecutor.start() }
  }

  private fun scheduleShutdownHook() {
    Runtime.getRuntime().addShutdownHook(thread(start = false) {
      runBlocking { logger.info { "Shutting down..." } }
    })
  }
}
