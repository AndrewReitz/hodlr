package cash.andrew.hodlr.http.coinbase

import cash.andrew.hodlr.http.coinbase.model.Account
import cash.andrew.hodlr.http.coinbase.model.DepositRequest
import cash.andrew.hodlr.http.coinbase.model.DepositResponse
import cash.andrew.hodlr.http.coinbase.model.PaymentMethod
import cash.andrew.hodlr.http.coinbase.model.PlaceOrderRequest
import cash.andrew.hodlr.http.coinbase.model.PlacedOrderData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Requires authorization headers
interface CoinbaseProPrivateService {

    @GET("accounts")
    suspend fun getAccounts(): List<Account>

    @GET("payment-methods")
    suspend fun getPaymentMethods(): List<PaymentMethod>

    @POST("deposits/payment-method")
    suspend fun depositFunds(@Body request: DepositRequest): DepositResponse

    @POST("orders")
    suspend fun placeOrder(@Body request: PlaceOrderRequest): PlacedOrderData
}
