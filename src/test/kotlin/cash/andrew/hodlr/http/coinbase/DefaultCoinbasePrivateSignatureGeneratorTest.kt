package cash.andrew.hodlr.http.coinbase

import cash.andrew.hodlr.DO_NOTHING_LOGGER
import cash.andrew.hodlr.config.CoinbaseConfig
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

class DefaultCoinbasePrivateSignatureGeneratorTest {

  private val config = CoinbaseConfig(
      key = "key",
      secret = "secret",
      passphrase = "b3BlbiBzZXNlbWU="
  )

  private val classUnderTest = DefaultCoinbasePrivateSignatureGenerator(
      config = config,
      logger = DO_NOTHING_LOGGER
  )

  @Test
  fun `should create signature`() {
    val signature = classUnderTest.sign(
        timeAsEpochSeconds = 9001,
        httpMethod = "GET",
        requestPath = "/accounts",
        requestBody = ""
    )

    signature shouldEqual "74rxuGRtEK49E7bHrLnoJER0GZKErkptk/KVXJcXUnk="
  }
}
