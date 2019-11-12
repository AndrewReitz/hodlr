package cash.andrew.hodlr.schedulers

import cash.andrew.hodlr.config.RecurringPurchaseConfig
import cash.andrew.hodlr.http.coinbase.AccountError
import cash.andrew.hodlr.http.coinbase.AccountSuccess
import cash.andrew.hodlr.http.coinbase.CoinbaseRepository
import cash.andrew.hodlr.http.coinbase.PlaceOrderError
import cash.andrew.hodlr.http.coinbase.PlaceOrderSuccess
import cash.andrew.hodlr.logging.Logger
import cash.andrew.hodlr.util.exhaustive
import cash.andrew.hodlr.util.isNotZero
import cash.andrew.hodlr.util.minutes
import org.jetbrains.annotations.TestOnly
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton
import cash.andrew.hodlr.http.coinbase.model.Account as CoinbaseAccount

@Singleton
class RecurringPurchaseTaskExecutor @Inject constructor(
    config: RecurringPurchaseConfig,
    private val coinbaseRepository: CoinbaseRepository,
    private val logger: Logger
) {

  private val currency = config.currency
  private val amount = config.amount
  private val assetsToPurchase = config.assetsToPurchase

  suspend fun start() {
    while (true) {
      10.minutes().delay()
      logger.trace { "Running account amount check" }
      val accountToUse = getAccountToUse() ?: continue
      logger.info { "${accountToUse.balance} has been deposited. Setting up trades." }
      placeOrders()
    }
  }

  @TestOnly
  suspend fun placeOrders() {
    assetsToPurchase.forEach { (asset, amountToPurchase) ->
      logger.info { "Placing order for $asset for $amountToPurchase" }

      val placeOrderResponse = coinbaseRepository.placeOrder(
          assetToPurchase = asset,
          purchaseCurrency = currency,
          funds = amountToPurchase
      )

      when (placeOrderResponse) {
        is PlaceOrderSuccess -> logger.info {
          with(placeOrderResponse.success) {
            when {
              settled -> "Order of $size $productId at $price has been purchased"
              else -> "Order of $funds for $productId has been created" // market buy?
            }
          }
        }
        is PlaceOrderError -> logger.error(placeOrderResponse.error) {
          "There was an error placing your order. Another will be attempted in 10 minutes."
        }
      }.exhaustive
    }
  }

  @TestOnly
  suspend fun getAccountToUse(): CoinbaseAccount? {
    val accountOrError = coinbaseRepository.getAccountIfThereIsAmountToInvest()

    val accountToUse: CoinbaseAccount? = when (accountOrError) {
      is Account -> accountOrError.account
      is Error -> {
        logger.error(accountOrError.error) { "There was an error loading accounts, attempting again in 10 minutes" }
        return null
      }
    }.exhaustive

    if (accountToUse == null) {
      logger.debug { "No account was found for currency $currency that had a non-zero balance" }
      return null
    }

    return accountToUse
  }

  private suspend fun CoinbaseRepository.getAccountIfThereIsAmountToInvest(): AccountOrError = when (val response = getAccounts()) {
    is AccountSuccess -> response.success
        .asSequence()
        .filter { it.currency == currency }
        .onEach { logger.debug { "Got account: $it" } }
        .filter { it.balance.isNotZero() }
        .onEach { logger.debug { "After filtering for non-zero balance: $it" } }
        .firstOrNull { it.balance >= amount }
        .let { Account(it) }
    is AccountError -> Error(response.error)
  }
}

private sealed class AccountOrError
private data class Account(val account: CoinbaseAccount?) : AccountOrError()
private data class Error(val error: Exception) : AccountOrError()
