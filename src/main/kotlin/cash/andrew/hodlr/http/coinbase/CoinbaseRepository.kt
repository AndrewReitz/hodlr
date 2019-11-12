package cash.andrew.hodlr.http.coinbase

import cash.andrew.hodlr.config.CryptoCurrency
import cash.andrew.hodlr.http.coinbase.model.Account
import cash.andrew.hodlr.http.coinbase.model.PaymentMethod
import cash.andrew.hodlr.http.coinbase.model.DepositRequest
import cash.andrew.hodlr.http.coinbase.model.FiatCurrency
import cash.andrew.hodlr.http.coinbase.model.DepositResponse as Deposit
import cash.andrew.hodlr.http.coinbase.model.Historic
import cash.andrew.hodlr.http.coinbase.model.PlaceOrderRequest
import cash.andrew.hodlr.http.coinbase.model.PlacedOrderData
import cash.andrew.hodlr.http.coinbase.model.Product
import java.math.BigDecimal
import java.math.RoundingMode

interface CoinbaseRepository {
  suspend fun getAccounts(): AccountResponse

  suspend fun getPaymentMethods(): PaymentResponse

  suspend fun depositFunds(amount: BigDecimal, currency: FiatCurrency, paymentMethodId: String): DepositResponse

  suspend fun placeOrder(
      assetToPurchase: CryptoCurrency,
      purchaseCurrency: FiatCurrency,
      funds: BigDecimal
  ): PlaceOrderResponse

  suspend fun getProducts(): ProductResponse

  suspend fun getProductHistory(productId: CryptoCurrency): HistoryResponse
}

class DefaultCoinbaseRepository(
    private val publicService: CoinbaseProPublicService,
    private val privateService: CoinbaseProPrivateService
) : CoinbaseRepository {

  override suspend fun getAccounts(): AccountResponse = try {
    AccountSuccess(privateService.getAccounts())
  } catch (e: Exception) {
    AccountError(e)
  }

  override suspend fun getPaymentMethods(): PaymentResponse = try {
    PaymentSuccess(privateService.getPaymentMethods())
  } catch (e: Exception) {
    PaymentError(e)
  }

  override suspend fun depositFunds(amount: BigDecimal, currency: FiatCurrency, paymentMethodId: String): DepositResponse {
    val request = DepositRequest(
        amount = amount.setScale(2, RoundingMode.HALF_UP).toDouble(),
        currency = currency,
        paymentMethodId = paymentMethodId
    )

    return try {
      DepositSuccess(privateService.depositFunds(request))
    } catch (e: Exception) {
      DepositError(e)
    }
  }

  override suspend fun placeOrder(
      assetToPurchase: CryptoCurrency,
      purchaseCurrency: FiatCurrency,
      funds: BigDecimal
  ): PlaceOrderResponse {
    val request = PlaceOrderRequest(
        productId = "$assetToPurchase-$purchaseCurrency",
        funds = funds.setScale(2, RoundingMode.HALF_UP).toPlainString()
    )

    return try {
      privateService.placeOrder(request)
          .let { PlaceOrderSuccess(it) }
    } catch (e: Exception) {
      PlaceOrderError(e)
    }
  }

  override suspend fun getProducts(): ProductResponse = try {
    ProductSuccess(publicService.getProducts())
  } catch (e: Exception) {
    ProductError(e)
  }

  override suspend fun getProductHistory(productId: CryptoCurrency): HistoryResponse = try {
    HistorySuccess(publicService.getHistoricData(productId))
  } catch (e: Exception) {
    HistoryError(e)
  }
}

sealed class PlaceOrderResponse
data class PlaceOrderSuccess(val success: PlacedOrderData) : PlaceOrderResponse()
data class PlaceOrderError(val error: Exception) : PlaceOrderResponse()

sealed class AccountResponse
data class AccountSuccess(val success: List<Account>) : AccountResponse()
data class AccountError(val error: Exception) : AccountResponse()

sealed class DepositResponse
data class DepositSuccess(val success: Deposit) : DepositResponse()
data class DepositError(val error: Exception) : DepositResponse()

sealed class PaymentResponse
data class PaymentSuccess(val paymentMethods: List<PaymentMethod>) : PaymentResponse()
data class PaymentError(val error: Exception) : PaymentResponse()

sealed class HistoryResponse
data class HistorySuccess(val history: List<Historic>) : HistoryResponse()
data class HistoryError(val error: Exception) : HistoryResponse()

sealed class ProductResponse
data class ProductError(val error: Exception) : ProductResponse()
data class ProductSuccess(val products: List<Product>) : ProductResponse()
