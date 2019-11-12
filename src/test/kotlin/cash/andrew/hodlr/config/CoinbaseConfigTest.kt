package cash.andrew.hodlr.config

import org.junit.jupiter.api.Test
import okio.ByteString.Companion.encodeUtf8
import org.amshove.kluent.`should be equal to`

class CoinbaseConfigTest {

  @Test
  fun `should base 64 decode secret`() {
    val expected = "Hello World!"

    val classUnderTest = CoinbaseConfig(
        key = "key".toCharArray(),
        secret = expected.encodeUtf8().base64().toCharArray(),
        passphrase = "passphrase".toCharArray()
    )

    val result = classUnderTest.base64DecodedSecret

    result.utf8() `should be equal to` expected
  }
}