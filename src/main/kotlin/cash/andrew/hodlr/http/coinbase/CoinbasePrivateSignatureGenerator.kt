package cash.andrew.hodlr.http.coinbase

import cash.andrew.hodlr.config.CoinbaseConfig
import cash.andrew.hodlr.logging.Logger
import dagger.Module
import okio.ByteString.Companion.encodeUtf8
import javax.inject.Inject
import javax.inject.Singleton

interface CoinbasePrivateSignatureGenerator {
  fun sign(
      timeAsEpochSeconds: Long,
      httpMethod: String,
      requestPath: String,
      requestBody: String
  ): String
}

/**
 * Signs requests for authenticated api methods.
 *
 * See https://docs.pro.coinbase.com/#signing-a-message
 */
class DefaultCoinbasePrivateSignatureGenerator(
    private val config: CoinbaseConfig,
    private val logger: Logger
) : CoinbasePrivateSignatureGenerator {

  override fun sign(
      timeAsEpochSeconds: Long,
      httpMethod: String,
      requestPath: String,
      requestBody: String
  ): String {
    logger.trace {
      "sign(timeAsEpockSeconds=$timeAsEpochSeconds, " +
          "httpMethod=$httpMethod, " +
          "requestPath=$requestPath, " +
          "requestBody=$requestBody"
    }

    val prehash = "$timeAsEpochSeconds$httpMethod$requestPath$requestBody"
    logger.trace { "prehash=$prehash" }

    return prehash.encodeUtf8()
        .hmacSha256(config.base64DecodedSecret)
        .base64()
  }
}
