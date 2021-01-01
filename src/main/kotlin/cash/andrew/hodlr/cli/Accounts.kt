package cash.andrew.hodlr.cli

import cash.andrew.hodlr.component
import cash.andrew.hodlr.http.coinbase.CoinbaseRepository
import cash.andrew.hodlr.util.exhaustive
import cash.andrew.hodlr.http.coinbase.PaymentError
import cash.andrew.hodlr.http.coinbase.PaymentSuccess
import cash.andrew.hodlr.logging.Logger
import cash.andrew.hodlr.util.ShutdownHook
import cash.andrew.hodlr.util.defaultLazy
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.runBlocking

class Accounts : CliktCommand() {

  private val coinbaseRepository: CoinbaseRepository by defaultLazy { component.coinbaseRepository }
  private val logger: Logger by defaultLazy { component.logger }
  private val shutdownHook: ShutdownHook by defaultLazy { component.shutdownHook }

  override fun run() = runBlocking {
    when (val response = coinbaseRepository.getPaymentMethods()) {
      is PaymentSuccess -> {
        if (response.paymentMethods.isEmpty()) {
          echo("No paymentMethods have been found. You need to set one up at https://www.coinbase.com/join/reitz_b")
        }

        response.paymentMethods.forEach {
          echo("PaymentMethod: ${it.name}, currency: ${it.currency}, id: ${it.id}")
        }
      }
      is PaymentError -> {
        logger.error(response.error) { "Error when trying to load paymentMethods" }
        echo("There was an error trying to load paymentMethods")
        echo("Try running with --error for more details")
      }
    }.exhaustive

    shutdownHook()
  }
}
