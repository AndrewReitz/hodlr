package cash.andrew.hodlr.http.coinbase

import cash.andrew.hodlr.config.CoinbaseConfig
import cash.andrew.hodlr.util.asString
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.Charsets.UTF_8

/**
 * Adds required headers for accessing private apis for an account.
 */
@Singleton
class CoinbasePrivateApiRequestHeaderInterceptor @Inject constructor(
    private val config: CoinbaseConfig,
    private val signatureGenerator: CoinbasePrivateSignatureGenerator,
    private val clock: Clock = Clock.systemDefaultZone()
) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    val requestBody = request.body
    val buffer = Buffer()
    requestBody?.writeTo(buffer)
    val body = buffer.readString(UTF_8)

    val method = request.method
    val requestPath = request.url.encodedPath
    val timeStamp = Instant.now(clock).epochSecond

    val signature = signatureGenerator.sign(
        timeAsEpochSeconds = timeStamp,
        httpMethod = method,
        requestBody = body,
        requestPath = requestPath
    )

    val newRequest = request
        .newBuilder()
        .addHeader("CB-ACCESS-KEY", config.key.asString())
        .addHeader("CB-ACCESS-SIGN", signature)
        .addHeader("CB-ACCESS-TIMESTAMP", "$timeStamp")
        .addHeader("CB-ACCESS-PASSPHRASE", config.passphrase.asString())
        .build()

    return chain.proceed(newRequest)
  }
}

