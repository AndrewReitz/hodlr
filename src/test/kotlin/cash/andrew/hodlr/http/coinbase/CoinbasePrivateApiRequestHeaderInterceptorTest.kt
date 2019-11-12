package cash.andrew.hodlr.http.coinbase

import cash.andrew.hodlr.TestClock
import cash.andrew.hodlr.config.CoinbaseConfig
import cash.andrew.hodlr.stub
import cash.andrew.hodlr.util.asString
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import java.time.Instant

class CoinbasePrivateApiRequestHeaderInterceptorTest {

  @Test
  fun `should add keybase private access headers to request`() {
    val response = classUnderTest.intercept(testChain)

    with(response.request) {
      header("CB-ACCESS-KEY") shouldEqual config.key.asString()
      header("CB-ACCESS-SIGN") shouldEqual "signature"
      header("CB-ACCESS-TIMESTAMP") shouldEqual "9001"
      header("CB-ACCESS-PASSPHRASE") shouldEqual config.passphrase.asString()
    }
  }

  private val testChain = object: Interceptor.Chain by stub() {
    override fun request(): Request = Request.Builder()
        .url("http://example.com")
        .build()

    override fun proceed(request: Request): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_2)
        .code(200)
        .message("yay!")
        .build()
  }

  private val testClock = object : TestClock() {
    override fun instant(): Instant = Instant.ofEpochSecond(9001)
  }

  private val testSignatureGenerator = object : CoinbasePrivateSignatureGenerator {
    override fun sign(
        timeAsEpochSeconds: Long,
        httpMethod: String,
        requestPath: String,
        requestBody: String
    ): String = "signature"
  }

  private val config = CoinbaseConfig(
      key = "key",
      secret = "b3BlbiBzZXNlbWU=",
      passphrase = "passphrase"
  )

  private val classUnderTest = CoinbasePrivateApiRequestHeaderInterceptor(
      config = config,
      signatureGenerator = testSignatureGenerator,
      clock = testClock
  )
}
