package cash.andrew.hodlr.http.coinbase

import cash.andrew.hodlr.config.CryptoCurrency
import cash.andrew.hodlr.http.coinbase.model.Account
import cash.andrew.hodlr.http.coinbase.model.DepositRequest
import cash.andrew.hodlr.http.coinbase.model.FiatCurrency
import cash.andrew.hodlr.http.coinbase.model.Historic
import cash.andrew.hodlr.http.coinbase.model.OrderType
import cash.andrew.hodlr.http.coinbase.model.DepositResponse as ApiDepositeResponse
import cash.andrew.hodlr.http.coinbase.model.PaymentMethod
import cash.andrew.hodlr.http.coinbase.model.PlaceOrderRequest
import cash.andrew.hodlr.http.coinbase.model.PlacedOrderData
import cash.andrew.hodlr.http.coinbase.model.Product
import cash.andrew.hodlr.http.coinbase.model.Side
import cash.andrew.hodlr.stub
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import java.io.IOException
import java.math.BigDecimal
import java.time.Instant
import java.util.Date

class DefaultCoinbaseRepositoryTest {

  @Test
  fun `should get accounts`() = runBlocking<Unit> {
    val privateService = object : CoinbaseProPrivateService by stub() {
      override suspend fun getAccounts(): List<Account> = listOf()
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = stub(),
        privateService = privateService
    )

    val result = classUnderTest.getAccounts()

    result shouldEqual AccountSuccess(listOf())
  }

  @Test
  fun `should return error when there is an issue getting accounts`() = runBlocking<Unit> {
    val expectedException = IOException("Oh No!")

    val privateService = object : CoinbaseProPrivateService by stub() {
      override suspend fun getAccounts(): List<Account> = throw expectedException
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = stub(),
        privateService = privateService
    )

    val result = classUnderTest.getAccounts()

    result shouldEqual AccountError(expectedException)
  }

  @Test
  fun `should get payment methods`() = runBlocking<Unit> {
    val privateService = object : CoinbaseProPrivateService by stub() {
      override suspend fun getPaymentMethods(): List<PaymentMethod> = listOf()
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = stub(),
        privateService = privateService
    )

    val paymentRepository = classUnderTest.getPaymentMethods()

    paymentRepository shouldEqual PaymentSuccess(listOf())
  }

  @Test
  fun `should return error when there is an issue getting payment methods`() = runBlocking<Unit> {
    val expectedException = IOException("Oh no!")

    val privateService = object : CoinbaseProPrivateService by stub() {
      override suspend fun getPaymentMethods(): List<PaymentMethod> = throw expectedException
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = stub(),
        privateService = privateService
    )

    val paymentRepository = classUnderTest.getPaymentMethods()

    paymentRepository shouldEqual PaymentError(expectedException)
  }

  @Test
  fun `should deposit funds`() = runBlocking<Unit> {
    val expectedResponse = ApiDepositeResponse(
        id = "e636f06d-e6dc-4908-b2f9-4d5db14ab496",
        amount = "10",
        currency = "USD",
        payoutAt = Date.from(Instant.ofEpochSecond(9000))
    )

    val privateService = object : CoinbaseProPrivateService by stub() {
      override suspend fun depositFunds(request: DepositRequest): ApiDepositeResponse {
        request.amount shouldEqualTo 10.00
        return expectedResponse
      }
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = stub(),
        privateService = privateService
    )

    val result = classUnderTest.depositFunds(10.toBigDecimal(), FiatCurrency.USD, "0000-000-0000")

    result shouldEqual DepositSuccess(expectedResponse)
  }

  @Test
  fun `should return error when there is an issue depositing funds`() = runBlocking<Unit> {
    val expectedResponse = ApiDepositeResponse(
        id = "e636f06d-e6dc-4908-b2f9-4d5db14ab496",
        amount = "10",
        currency = "USD",
        payoutAt = Date.from(Instant.ofEpochSecond(9000))
    )

    val privateService = object : CoinbaseProPrivateService by stub() {
      override suspend fun depositFunds(request: DepositRequest): ApiDepositeResponse {
        request.amount shouldEqualTo 10.01
        return expectedResponse
      }
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = stub(),
        privateService = privateService
    )

    val result = classUnderTest.depositFunds(10.005.toBigDecimal(), FiatCurrency.USD, "0000-000-0000")

    result shouldEqual DepositSuccess(expectedResponse)
  }

  @Test
  fun `should place an order`() = runBlocking<Unit> {
    val expected = PlacedOrderData(
        id = "id",
        price = "10",
        size = "100",
        productId = "productId",
        side = Side.buy,
        type = OrderType.limit,
        postOnly = false,
        createdAt = Date.from(Instant.ofEpochSecond(1000)),
        filledSize = "1",
        fillFees = "1",
        executedValue = "1000.00",
        status = "Success",
        settled = true,
        funds = "999.97",
        specified_funds = "1000.00"
    )

    val privateService = object : CoinbaseProPrivateService by stub() {
      override suspend fun placeOrder(request: PlaceOrderRequest): PlacedOrderData {
        request shouldEqual PlaceOrderRequest(
            type = OrderType.market,
            side = Side.buy,
            productId = "BTC-USD",
            funds = "10.00"
        )
        return expected
      }
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = stub(),
        privateService = privateService
    )

    val response = classUnderTest.placeOrder(
        assetToPurchase = CryptoCurrency.BTC,
        purchaseCurrency = FiatCurrency.USD,
        funds = BigDecimal("10.004")
    )

    response shouldEqual PlaceOrderSuccess(expected)
  }

  @Test
  fun `should return an error when there is an issue placing an order`() = runBlocking<Unit> {
    val expected = IOException("http error!")

    val privateService = object : CoinbaseProPrivateService by stub() {
      override suspend fun placeOrder(request: PlaceOrderRequest): PlacedOrderData {
        request shouldEqual PlaceOrderRequest(
            type = OrderType.market,
            side = Side.buy,
            productId = "BHC-USD",
            funds = "10.01"
        )
        throw expected
      }
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = stub(),
        privateService = privateService
    )

    val response = classUnderTest.placeOrder(
        assetToPurchase = CryptoCurrency.BHC,
        purchaseCurrency = FiatCurrency.USD,
        funds = BigDecimal("10.006")
    )

    response shouldEqual PlaceOrderError(expected)
  }

  @Test
  fun `should get products`() = runBlocking<Unit> {
    val publicService = object : CoinbaseProPublicService by stub() {
      override suspend fun getProducts(): List<Product> = listOf()
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = publicService,
        privateService = stub()
    )

    val result = classUnderTest.getProducts()

    result shouldEqual ProductSuccess(listOf())
  }

  @Test
  fun `should return an error when there is an issue getting products`() = runBlocking<Unit> {
    val expectedException = IOException("oh no!")

    val publicService = object : CoinbaseProPublicService by stub() {
      override suspend fun getProducts(): List<Product> = throw expectedException
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = publicService,
        privateService = stub()
    )

    val result = classUnderTest.getProducts()

    result shouldEqual ProductError(expectedException)
  }

  @Test
  fun `should get product history`() = runBlocking<Unit> {
    val publicService = object : CoinbaseProPublicService by stub() {
      override suspend fun getHistoricData(
          productId: CryptoCurrency,
          start: Date?,
          end: Date?,
          granularity: Int
      ): List<Historic> = listOf()
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = publicService,
        privateService = stub()
    )

    val result = classUnderTest.getProductHistory(CryptoCurrency.BTC)

    result shouldEqual HistorySuccess(listOf())
  }

  @Test
  fun `should return error when there is an issue getting product history`() = runBlocking<Unit> {
    val expectedException = IOException("Oh no!")

    val publicService = object : CoinbaseProPublicService by stub() {
      override suspend fun getHistoricData(
          productId: CryptoCurrency,
          start: Date?,
          end: Date?,
          granularity: Int
      ): List<Historic> = throw expectedException
    }

    val classUnderTest = DefaultCoinbaseRepository(
        publicService = publicService,
        privateService = stub()
    )

    val result = classUnderTest.getProductHistory(CryptoCurrency.BTC)

    result shouldEqual HistoryError(expectedException)
  }
}
