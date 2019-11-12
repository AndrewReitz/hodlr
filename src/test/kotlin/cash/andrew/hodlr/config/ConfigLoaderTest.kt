package cash.andrew.hodlr.config

import cash.andrew.hodlr.http.coinbase.model.FiatCurrency
import cash.andrew.hodlr.logging.LogLevel
import cash.andrew.hodlr.util.asString
import com.typesafe.config.ConfigException
import okhttp3.logging.HttpLoggingInterceptor
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.stream.Stream
import org.junit.jupiter.params.provider.Arguments.arguments

class ConfigLoaderTest {

  private val classUnderTest = ConfigLoader()

  @TempDir
  lateinit var dir: File

  @Test
  fun `should parse coinbase config successfully`() {
    val expected = CoinbaseConfig(
        key = "keyFromCoinbase",
        secret = "secretFromCoinbase",
        passphrase = "coinbasePassphrase"
    )

    val (coinbaseConfig: CoinbaseConfig) = loadApplicationConfig(
        """
      coinbase {
          passphrase = "${expected.passphrase.asString()}"
          secret = "${expected.secret.asString()}"
          key = "${expected.key.asString()}"
      }

      recurring-purchase {
          frequency = monthly
          day = 1
          account = 2d64486a-17c9-422b-8925-77d7d36541b2
          amount = 100
          currency = USD
          assets-to-purchase: {
              "BTC": 10,
              "LTC": 90
          }
      }
    """.trimIndent()
    )

    coinbaseConfig shouldEqual expected
  }

  @Suppress("UNUSED_PARAMETER")
  @ParameterizedTest(name = "{index} => should throw exception missing coinbase config is missing {0}")
  @MethodSource("exceptionInCoinbaseConfigProvider")
  fun `require coinbase config values`(missing: String, config: String, expected: String) {
    val exception = assertThrows<ConfigException.Missing> {
      loadApplicationConfig(config)
    }

    exception.message!! shouldContain (expected)
  }

  @Test
  fun `should load default logging config`() {
    val (_, loggingConfig) = loadApplicationConfig("""
      coinbase {
          passphrase = "pasphrase"
          secret = "secret"
          key = "key"
      }

      recurring-purchase {
          frequency = monthly
          day = 1
          account = 2d64486a-17c9-422b-8925-77d7d36541b2
          amount = 100
          currency = USD
          assets-to-purchase: {
              "BTC": 100
          }
      }
    """.trimIndent()
    )

    loggingConfig shouldEqual LoggingConfig()
  }

  @Test
  fun `should load logLevel in logging config`() {
    val expected = LoggingConfig(logLevel = LogLevel.WARN)
    val (_, loggingConfig) = loadApplicationConfig("""
      logging {
          logLevel = ${expected.logLevel}
      }
      
      coinbase {
          passphrase = "pasphrase"
          secret = "secret"
          key = "key"
      }

      recurring-purchase {
          frequency = monthly
          day = 1
          account = 2d64486a-17c9-422b-8925-77d7d36541b2
          amount = 100
          currency = USD
          assets-to-purchase: {
              "BTC": 100
          }
      }
    """.trimIndent()
    )

    loggingConfig shouldEqual expected
  }

  @Test
  fun `should load httpLogLevel in logging config`() {
    val expected = LoggingConfig(httpLogLevel = HttpLoggingInterceptor.Level.HEADERS)
    val (_, loggingConfig) = loadApplicationConfig("""
      logging {
          httpLogLevel = ${expected.httpLogLevel}
      }
      
      coinbase {
          passphrase = "pasphrase"
          secret = "secret"
          key = "key"
      }

      recurring-purchase {
          frequency = monthly
          day = 1
          account = 2d64486a-17c9-422b-8925-77d7d36541b2
          amount = 100
          currency = USD
          assets-to-purchase: {
              "BTC": 100
          }
      }
    """.trimIndent()
    )

    loggingConfig shouldEqual expected
  }

  @Test
  fun `should load recurring purchase config`() {
    val expected = RecurringPurchaseConfig(
        frequency = Frequency.weekly,
        account = "c0836544-348d-43cd-9067-346c7633be37",
        amount = 100.0.toBigDecimal(),
        currency = FiatCurrency.USD,
        assetsToPurchase = mapOf(
            CryptoCurrency.BTC to 33.toBigDecimal(),
            CryptoCurrency.LTC to 33.toBigDecimal(),
            CryptoCurrency.ZRX to 34.toBigDecimal()
        )
    )

    val (_, _, recurringPurchaseConfig) = loadApplicationConfig("""
      recurring-purchase {
          frequency = ${expected.frequency}
          account = ${expected.account}
          amount = ${expected.amount}
          currency = ${expected.currency}
          assets-to-purchase: {
              ${
    expected.assetsToPurchase
        .toList()
        .joinToString(postfix = "\n") { (key, value) -> "$key: $value" }
    }
          }
      }
      coinbase {
          passphrase = "pasphrase"
          secret = "secret"
          key = "key"
      }
    """.trimIndent())

    recurringPurchaseConfig shouldEqual expected
  }

  @Suppress("UNUSED_PARAMETER")
  @ParameterizedTest(name = "{index} => should throw exception missing recurringPurchase config is missing {0}")
  @MethodSource("execptionInRecurringPurhcaseConfigProvider")
  fun `require recurringPurchase config values`(missing: String, config: String, message: String) {
    val exception = assertThrows<ConfigException.Missing> {
      loadApplicationConfig(config)
    }

    exception.message!! shouldContain message
  }

  @Test
  fun `should load default telegram config`() {
    val (_, _, _, _, telegramConfig) = loadApplicationConfig("""
      coinbase {
          passphrase = "pasphrase"
          secret = "secret"
          key = "key"
      }

      recurring-purchase {
          frequency = monthly
          day = 1
          account = 2d64486a-17c9-422b-8925-77d7d36541b2
          amount = 100
          currency = USD
          assets-to-purchase: {
              "BTC": 100
          }
      }
    """.trimIndent()
    )

    telegramConfig shouldEqual null
  }

  @Test
  fun `should load telegram config`() {
    val expected = TelegramConfig(
        userId = 1337,
        apiToken = "9bccf6f8-8ef1-4c6f-a66f-c72a29dec783"
    )

    val (_, _, _, _, telegramConfig) = loadApplicationConfig("""
      telegram {
          user-id = ${expected.userId}
          api-token = ${expected.apiToken}
      }
      
      coinbase {
          passphrase = "pasphrase"
          secret = "secret"
          key = "key"
      }

      recurring-purchase {
          frequency = monthly
          day = 1
          account = 2d64486a-17c9-422b-8925-77d7d36541b2
          amount = 100
          currency = USD
          assets-to-purchase: {
              "BTC": 100
          }
      }
    """.trimIndent()
    )

    telegramConfig shouldEqual expected
  }

  @Suppress("UNUSED_PARAMETER")
  @ParameterizedTest(name = "{index} => should throw exception missing telegram config is missing {0}")
  @MethodSource("execptionInTelegramConfigProvider")
  fun `require telegram config values`(missing: String, config: String, message: String) {
    val exception = assertThrows<ConfigException.Missing> { loadApplicationConfig(config) }
    exception.message!! shouldContain message
  }

  @Test
  fun `should load default sandbox values`() {
    val (_, _, _, sandbox, _) = loadApplicationConfig("""
      coinbase {
          passphrase = "pasphrase"
          secret = "secret"
          key = "key"
      }

      recurring-purchase {
          frequency = monthly
          day = 1
          account = 2d64486a-17c9-422b-8925-77d7d36541b2
          amount = 100
          currency = USD
          assets-to-purchase: {
              "BTC": 100
          }
      }
    """.trimIndent()
    )

    sandbox shouldEqual false
  }

  @Test
  fun `should load sandbox values`() {
    val (_, _, _, sandbox, _) = loadApplicationConfig("""
      sandbox = true
      coinbase {
          passphrase = "pasphrase"
          secret = "secret"
          key = "key"
      }

      recurring-purchase {
          frequency = monthly
          day = 1
          account = 2d64486a-17c9-422b-8925-77d7d36541b2
          amount = 100
          currency = USD
          assets-to-purchase: {
              "BTC": 100
          }
      }
    """.trimIndent()
    )

    sandbox shouldEqual true
  }

  private fun loadApplicationConfig(config: String): ConfigContainer =
      File(dir, "application.conf")
          .also { it.writeText(config) }
          .let { classUnderTest.load(it) }

  companion object {
    @JvmStatic
    fun exceptionInCoinbaseConfigProvider(): Stream<Arguments> {
      return Stream.of(
          arguments(
              "key",
              """coinbase {
                  passphrase = "passphrase"
                  secret = "secret"
            }
            """,
              "No configuration setting found for key 'key'"
          ),
          arguments(
              "passphrase",
              """coinbase {
                key = "key"
                secret = "secret"
            }
            """,
              "No configuration setting found for key 'passphrase'"
          ),
          arguments("secret",
              """coinbase {
                key = "key"
                passphrase = "passphrase"
            }  
            """,
              "No configuration setting found for key 'secret'"
          )
      )
    }

    @JvmStatic
    fun execptionInRecurringPurhcaseConfigProvider(): Stream<Arguments> {
      return Stream.of(
          arguments(
              "frequency",
              """
            recurring-purchase {
                account = 15
                amount = 100
                currency = USD
                assets-to-purchase: {
                    BTC: 100
                }
            }
            coinbase {
                passphrase = "pasphrase"
                secret = "secret"
                key = "key"
            }
          """.trimMargin(),
              "No configuration setting found for key 'frequency'"
          ),
          arguments(
              "account",
              """
            recurring-purchase {
                frequency = daily
                amount = 100
                currency = USD
                assets-to-purchase: {
                    BTC: 100
                }
            }
            coinbase {
                passphrase = "pasphrase"
                secret = "secret"
                key = "key"
            }
          """.trimMargin(),
              " No configuration setting found for key 'account'"
          ),
          arguments(
              "amount",
              """
            recurring-purchase {
                frequency = weekly
                account = 15
                currency = USD
                assets-to-purchase: {
                    BTC: 100
                }
            }
            coinbase {
                passphrase = "pasphrase"
                secret = "secret"
                key = "key"
            }
          """.trimMargin(),
              "No configuration setting found for key 'amount'"
          ),
          arguments(
              "currency",
              """
            recurring-purchase {
                frequency = weekly
                account = 15
                amount = 100
                assets-to-purchase: {
                    BTC: 100
                }
            }
            coinbase {
                passphrase = "pasphrase"
                secret = "secret"
                key = "key"
            }
          """.trimMargin(),
              "No configuration setting found for key 'currency'"
          ),
          arguments(
              "assets-to-purchase",
              """
            recurring-purchase {
                frequency = weekly
                account = 15
                amount = 100
                currency = USD
            }
            coinbase {
                passphrase = "pasphrase"
                secret = "secret"
                key = "key"
            }
          """.trimMargin(),
              "No configuration setting found for key 'assets-to-purchase'"
          )
      )
    }

    @JvmStatic
    fun execptionInTelegramConfigProvider(): Stream<Arguments> {
      return Stream.of(
          arguments(
              "userId",
              """ telegram {
                      api-token = spooooooky
                  }
                  
                  coinbase {
                      passphrase = "pasphrase"
                      secret = "secret"
                      key = "key"
                  }
            
                  recurring-purchase {
                      frequency = monthly
                      day = 1
                      account = 2d64486a-17c9-422b-8925-77d7d36541b2
                      amount = 100
                      currency = USD
                      assets-to-purchase: {
                          "BTC": 100
                      }
                  }
                """.trimIndent(),
              "No configuration setting found for key 'user-id'"
          ),
          arguments(
              "api-token",
              """ telegram {
                      user-id = 9001
                  }
                  
                  coinbase {
                      passphrase = "pasphrase"
                      secret = "secret"
                      key = "key"
                  }
            
                  recurring-purchase {
                      frequency = monthly
                      day = 1
                      account = 2d64486a-17c9-422b-8925-77d7d36541b2
                      amount = 100
                      currency = USD
                      assets-to-purchase: {
                          "BTC": 100
                      }
                  }
                """.trimIndent(),
              "No configuration setting found for key 'api-token'"
          )
      )
    }
  }
}
