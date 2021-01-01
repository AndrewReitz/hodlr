package cash.andrew.hodlr.schedulers

import cash.andrew.hodlr.DO_NOTHING_LOGGER
import cash.andrew.hodlr.OutputStreamTest
import cash.andrew.hodlr.config.CryptoCurrency
import cash.andrew.hodlr.config.Frequency
import cash.andrew.hodlr.config.RecurringPurchaseConfig
import cash.andrew.hodlr.http.coinbase.AccountError
import cash.andrew.hodlr.http.coinbase.AccountSuccess
import cash.andrew.hodlr.http.coinbase.CoinbaseRepository
import cash.andrew.hodlr.http.coinbase.PlaceOrderError
import cash.andrew.hodlr.http.coinbase.PlaceOrderResponse
import cash.andrew.hodlr.http.coinbase.PlaceOrderSuccess
import cash.andrew.hodlr.http.coinbase.model.Account
import cash.andrew.hodlr.http.coinbase.model.FiatCurrency
import cash.andrew.hodlr.http.coinbase.model.OrderType
import cash.andrew.hodlr.http.coinbase.model.PlacedOrderData
import cash.andrew.hodlr.http.coinbase.model.Side
import cash.andrew.hodlr.logging.ConsoleLogger
import cash.andrew.hodlr.stub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.io.IOException
import java.math.BigDecimal
import java.util.Date

@ExperimentalCoroutinesApi
class RecurringPurchaseTaskExecutorTest : OutputStreamTest() {

  @Test
  fun `should return an account on successful network call and account balance is greater than 0`() = runBlockingTest {

      val expected = Account(
          id = "id",
          currency = FiatCurrency.USD,
          balance = 100.toBigDecimal(),
          available = 100.toBigDecimal(),
          hold = 0.toBigDecimal(),
          profileId = "profileId"
      )

      val config = RecurringPurchaseConfig(
          frequency = Frequency.daily,
          account = "mine",
          currency = FiatCurrency.USD,
          amount = 100.toBigDecimal(),
          assetsToPurchase = mapOf(
              CryptoCurrency.ETH to 100.toBigDecimal()
          )
      )

      val coinbaseRepository = object : CoinbaseRepository by stub() {
          override suspend fun getAccounts() = AccountSuccess(listOf(expected))
      }

      val classUnderTest = RecurringPurchaseTaskExecutor(
          config = config,
          logger = DO_NOTHING_LOGGER,
          coinbaseRepository = coinbaseRepository
      )

      val result = classUnderTest.getAccountToUse()
      result shouldBeEqualTo expected
  }

  @Test
  fun `should return an null when there is a 0 account balance`() = runBlockingTest {
      val config = RecurringPurchaseConfig(
          frequency = Frequency.daily,
          account = "mine",
          currency = FiatCurrency.USD,
          amount = 100.toBigDecimal(),
          assetsToPurchase = mapOf(
              CryptoCurrency.ETH to 100.toBigDecimal()
          )
      )

      val coinbaseRepository = object : CoinbaseRepository by stub() {
          override suspend fun getAccounts() = AccountSuccess(
              listOf(
                  Account(
                      id = "id",
                      currency = FiatCurrency.USD,
                      balance = 0.toBigDecimal(),
                      available = 0.toBigDecimal(),
                      hold = 0.toBigDecimal(),
                      profileId = "profileId"
                  )
              )
          )
      }

      val classUnderTest = RecurringPurchaseTaskExecutor(
          config = config,
          logger = DO_NOTHING_LOGGER,
          coinbaseRepository = coinbaseRepository
      )

      val result = classUnderTest.getAccountToUse()
      result shouldBeEqualTo null
  }

  @Test
  fun `should return null when there is an error loading accounts`() = runBlockingTest {
      val config = RecurringPurchaseConfig(
          frequency = Frequency.daily,
          account = "mine",
          currency = FiatCurrency.USD,
          amount = 100.toBigDecimal(),
          assetsToPurchase = mapOf(
              CryptoCurrency.ETH to 100.toBigDecimal()
          )
      )

      val coinbaseRepository = object : CoinbaseRepository by stub() {
          override suspend fun getAccounts() = AccountError(IOException("🏴‍☠️"))
      }

      val classUnderTest = RecurringPurchaseTaskExecutor(
          config = config,
          logger = DO_NOTHING_LOGGER,
          coinbaseRepository = coinbaseRepository
      )

      val result = classUnderTest.getAccountToUse()
      result shouldBeEqualTo null
  }

  @Test
  fun `should place orders not settled`() = runBlockingTest {
    val config = RecurringPurchaseConfig(
        frequency = Frequency.daily,
        account = "mine",
        currency = FiatCurrency.USD,
        amount = 100.toBigDecimal(),
        assetsToPurchase = mapOf(
            CryptoCurrency.BTC to 100.toBigDecimal()
        )
    )

    val coinbaseRepository = object : CoinbaseRepository by stub() {
      override suspend fun placeOrder(assetToPurchase: CryptoCurrency, purchaseCurrency: FiatCurrency, funds: BigDecimal): PlaceOrderResponse {
        return PlaceOrderSuccess(
            PlacedOrderData(
                id = "2fd8d9f0-8857-4076-bb93-600dbd67f470",
                price = "100",
                size = "1",
                productId = "BTC-USD",
                side = Side.buy,
                type = OrderType.market,
                postOnly = true,
                createdAt = Date(),
                fillFees = "0.0000000000000000",
                filledSize = "0.00000000",
                executedValue = "0.0000000000000000",
                status = "pending",
                settled = false,
                funds = "100",
                specified_funds = "1000.00"
            )
        )
      }
    }

    val classUnderTest = RecurringPurchaseTaskExecutor(
        config = config,
        logger = ConsoleLogger(),
        coinbaseRepository = coinbaseRepository
    )

    classUnderTest.placeOrders()

    errorText shouldBeEqualTo ""
    outputText shouldBeEqualTo """
      Placing order for BTC for 100
      Order of 100 for BTC-USD has been created
    """.trimIndent()
  }

  @Test
  fun `should place orders settled`() = runBlockingTest {
    val config = RecurringPurchaseConfig(
        frequency = Frequency.daily,
        account = "mine",
        currency = FiatCurrency.USD,
        amount = 100.toBigDecimal(),
        assetsToPurchase = mapOf(
            CryptoCurrency.BTC to 100.toBigDecimal()
        )
    )

    val coinbaseRepository = object : CoinbaseRepository by stub() {
      override suspend fun placeOrder(assetToPurchase: CryptoCurrency, purchaseCurrency: FiatCurrency, funds: BigDecimal): PlaceOrderResponse {
        return PlaceOrderSuccess(
            PlacedOrderData(
                id = "2fd8d9f0-8857-4076-bb93-600dbd67f470",
                price = "100",
                size = "1",
                productId = "BTC-USD",
                side = Side.buy,
                type = OrderType.market,
                postOnly = true,
                createdAt = Date(),
                fillFees = "0.0000000000000000",
                filledSize = "0.00000000",
                executedValue = "0.0000000000000000",
                status = "pending",
                settled = true,
                funds = "100",
                specified_funds = "1000.00"
            )
        )
      }
    }

    val classUnderTest = RecurringPurchaseTaskExecutor(
        config = config,
        logger = ConsoleLogger(),
        coinbaseRepository = coinbaseRepository
    )

    classUnderTest.placeOrders()

    errorText shouldBeEqualTo ""
    outputText shouldBeEqualTo """
          Placing order for BTC for 100
          Order of 1 BTC-USD at 100 has been purchased
        """.trimIndent()
  }

  @Test
  fun `should place orders and handle error`() = runBlockingTest {
    val config = RecurringPurchaseConfig(
        frequency = Frequency.daily,
        account = "mine",
        currency = FiatCurrency.USD,
        amount = 100.toBigDecimal(),
        assetsToPurchase = mapOf(
            CryptoCurrency.BTC to 100.toBigDecimal()
        )
    )

    val coinbaseRepository = object : CoinbaseRepository by stub() {
      override suspend fun placeOrder(
          assetToPurchase: CryptoCurrency,
          purchaseCurrency: FiatCurrency,
          funds: BigDecimal
      ) = PlaceOrderError(IOException("☠️"))
    }

    val classUnderTest = RecurringPurchaseTaskExecutor(
        config = config,
        logger = ConsoleLogger(),
        coinbaseRepository = coinbaseRepository
    )

    classUnderTest.placeOrders()

    errorText shouldBeEqualTo """
      There was an error placing your order. Another will be attempted in 10 minutes.
      java.io.IOException: ☠️
    """.trimIndent()
    outputText shouldBeEqualTo "Placing order for BTC for 100"
  }
}

