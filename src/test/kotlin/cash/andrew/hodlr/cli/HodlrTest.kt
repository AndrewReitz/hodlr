package cash.andrew.hodlr.cli

import cash.andrew.hodlr.OutputStreamTest
import cash.andrew.hodlr.config.ConfigNotFoundException
import com.typesafe.config.ConfigException
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HodlrTest : OutputStreamTest() {

  @Test
  fun `should fail to run when no configuration is provided`() {
    assertThrows<ConfigNotFoundException> {
      Hodlr().parse(listOf())
    }

    errorText shouldBeEqualTo "Configuration was not provided. Please provide one with --config"
    outputText shouldBeEqualTo ""
  }

  @Test
  fun `should fail when configuration is not correct`() {
    config.writeText("""
      nothingToSeeHere = "oops"
    """.trimIndent())

    assertThrows<ConfigException> {
      Hodlr().parse(listOf("--config", config.absolutePath))
    }

    errorText shouldContain "hodlr.conf: 1: No configuration setting found for key 'coinbase'"
    outputText shouldBeEqualTo ""
  }

  @Test
  fun `should run with no error`() {
    config.writeText("""
      coinbase {
          passphrase: coinbasePassphrase
          secret: SecretFromCoinbase
          key: keyFromCoinbase
      }
      
      recurring-purchase {
          frequency: monthly
          day: 13
          account: ACNT-ID-GOS-HRE
          amount: 10
          currency: USD
          assets-to-purchase { BTC : 10 }
      }
    """.trimIndent())

    Hodlr().parse(listOf("--config", config.absolutePath))

    errorText shouldBeEqualTo ""
    outputText shouldBeEqualTo ""
  }
}
