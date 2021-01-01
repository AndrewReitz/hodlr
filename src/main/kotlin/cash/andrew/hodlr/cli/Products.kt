package cash.andrew.hodlr.cli

import cash.andrew.hodlr.component
import cash.andrew.hodlr.http.coinbase.CoinbaseRepository
import cash.andrew.hodlr.util.exhaustive
import cash.andrew.hodlr.http.coinbase.ProductError
import cash.andrew.hodlr.http.coinbase.ProductSuccess
import cash.andrew.hodlr.logging.Logger
import cash.andrew.hodlr.util.ShutdownHook
import cash.andrew.hodlr.util.defaultLazy
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.runBlocking

class Products: CliktCommand() {

  private val coinbaseRepository: CoinbaseRepository by defaultLazy { component.coinbaseRepository }
  private val logger: Logger by defaultLazy { component.logger }
  private val shutdownHook: ShutdownHook by defaultLazy { component.shutdownHook }

  override fun run() = runBlocking {
    when (val response = coinbaseRepository.getProducts()) {
      is ProductSuccess -> {
        response.products.forEach {
          echo(it.id)
        }
      }
      is ProductError -> {
        logger.error(response.error) { "Error when trying to get products" }
        echo("There was an error trying to load payment methods")
        echo("Try running with --error for more details")
      }
    }.exhaustive

    shutdownHook()
  }
}
