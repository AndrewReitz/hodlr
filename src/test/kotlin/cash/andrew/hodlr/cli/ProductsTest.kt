package cash.andrew.hodlr.cli

import cash.andrew.hodlr.OutputStreamTest
import cash.andrew.hodlr.TestAppComponent
import cash.andrew.hodlr.component
import cash.andrew.hodlr.http.coinbase.CoinbaseRepository
import cash.andrew.hodlr.http.coinbase.ProductError
import cash.andrew.hodlr.http.coinbase.ProductResponse
import cash.andrew.hodlr.http.coinbase.ProductSuccess
import cash.andrew.hodlr.http.coinbase.model.Product
import cash.andrew.hodlr.stub
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.io.IOException

class ProductsTest : OutputStreamTest() {

  @Test
  fun `should fail when there is a network error`() {
    val coinbaseRepository = object : CoinbaseRepository by stub() {
      override suspend fun getProducts(): ProductResponse = ProductError(IOException("oh know!"))
    }

    component = object : TestAppComponent() {
      override val coinbaseRepository: CoinbaseRepository get() = coinbaseRepository
    }

    Products().parse(listOf())

    outputText shouldBeEqualTo """
      There was an error trying to load payment methods
      Try running with --error for more details"""
        .trimIndent()
        .trim()
    errorText shouldBeEqualTo ""
  }

  @Test
  fun `should run with no error`() {
    val coinbaseRepository = object : CoinbaseRepository by stub() {
      override suspend fun getProducts(): ProductResponse = ProductSuccess(
          listOf(
              Product(
                  id = "id",
                  base_currency = "USD",
                  quote_currency = "USD",
                  base_min_size = "10",
                  quote_increment = "100"
              )
          )
      )
    }

    component = object : TestAppComponent() {
      override val coinbaseRepository: CoinbaseRepository get() = coinbaseRepository
    }

    Products().parse(listOf())

    outputText shouldBeEqualTo "id"
    errorText shouldBeEqualTo ""
  }
}
