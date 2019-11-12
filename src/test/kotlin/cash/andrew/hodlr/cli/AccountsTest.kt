package cash.andrew.hodlr.cli

import cash.andrew.hodlr.OutputStreamTest
import cash.andrew.hodlr.TestAppComponent
import cash.andrew.hodlr.component
import cash.andrew.hodlr.http.coinbase.CoinbaseRepository
import cash.andrew.hodlr.http.coinbase.PaymentError
import cash.andrew.hodlr.http.coinbase.PaymentResponse
import cash.andrew.hodlr.http.coinbase.PaymentSuccess
import cash.andrew.hodlr.http.coinbase.model.PaymentMethod
import cash.andrew.hodlr.stub
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.io.IOException

class AccountsTest: OutputStreamTest() {

  @Test
  fun `when no payment methods available should print out sign up message`() {
    val coinbaseRepository = object : CoinbaseRepository by stub() {
      override suspend fun getPaymentMethods(): PaymentResponse = PaymentSuccess(listOf())
    }

    component = object : TestAppComponent() {
      override val coinbaseRepository: CoinbaseRepository get() = coinbaseRepository
    }

    Accounts().parse(listOf())

    outputText shouldBeEqualTo "No paymentMethods have been found. You need to set one up at https://www.coinbase.com/join/reitz_b"
    errorText shouldBeEqualTo ""
  }

  @Test
  fun `when there are payment methods available print them out`() {
    val coinbaseRepository = object : CoinbaseRepository by stub() {
      override suspend fun getPaymentMethods(): PaymentResponse = PaymentSuccess(
          listOf(
              PaymentMethod(
                  id = "af0cd74f-0541-48d6-bf0f-7d505ac3cc2a",
                  type = "Bank Account",
                  name = "Test Bank",
                  currency = "USD"
              )
          )
      )
    }

    component = object : TestAppComponent() {
      override val coinbaseRepository: CoinbaseRepository get() = coinbaseRepository
    }

    Accounts().parse(listOf())

    outputText shouldBeEqualTo "PaymentMethod: Test Bank, currency: USD, id: af0cd74f-0541-48d6-bf0f-7d505ac3cc2a"
    errorText shouldBeEqualTo ""
  }

  @Test
  fun `should fail when there is a network error`() {
    val coinbaseRepository = object : CoinbaseRepository by stub() {
      override suspend fun getPaymentMethods(): PaymentResponse = PaymentError(IOException("There was an error"))
    }

    component = object : TestAppComponent() {
      override val coinbaseRepository: CoinbaseRepository get() = coinbaseRepository
    }

    Accounts().parse(listOf())

    outputText shouldBeEqualTo """
      There was an error trying to load paymentMethods
      Try running with --error for more details
      """.trimIndent().trim()
    errorText shouldBeEqualTo ""
  }
}
