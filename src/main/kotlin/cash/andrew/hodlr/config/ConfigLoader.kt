package cash.andrew.hodlr.config

import cash.andrew.hodlr.util.getBooleanOrDefault
import cash.andrew.hodlr.util.getEnum
import cash.andrew.hodlr.util.getEnumOrDefault
import cash.andrew.hodlr.util.getIntOrDefault
import cash.andrew.hodlr.util.maybeConfig
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import java.io.File
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigLoader @Inject constructor() {
  fun load(configFile: File): ConfigContainer = with(ConfigFactory.parseFile(configFile)) {

    val coinbase = with(getConfig("coinbase")) {
      CoinbaseConfig(
          key = getString("key"),
          secret = getString("secret"),
          passphrase = getString("passphrase")
      )
    }

    val logging = maybeConfig("logging") {
      LoggingConfig(
          logLevel = getEnumOrDefault("logLevel", DEFAULT_LOG_LEVEL),
          httpLogLevel = getEnumOrDefault("httpLogLevel", NONE)
      )
    } ?: LoggingConfig()

    val recurringPurchase = with(getConfig("recurring-purchase")) {
      RecurringPurchaseConfig(
          frequency = getEnum("frequency"),
          day = getIntOrDefault("day", 1),
          account = getString("account"),
          amount = getDouble("amount").toBigDecimal(),
          currency = getEnum("currency"),
          assetsToPurchase = getAssetsToPurchase()
      )
    }

    val telegramConfig = maybeConfig("telegram") {
      TelegramConfig(
          userId = getLong("user-id"),
          apiToken = getString("api-token")
      )
    }

    ConfigContainer(
        coinbaseConfig = coinbase,
        loggingConfig = logging,
        recurringPurchaseConfig = recurringPurchase,
        sandbox = getBooleanOrDefault("sandbox", false),
        telegramConfig = telegramConfig
    )
  }

  private fun Config.getAssetsToPurchase(): Map<CryptoCurrency, BigDecimal> {
    return getConfig("assets-to-purchase")
        .root()
        .map { (key, value) ->
          val transformedValue = when (val unwrapped = value.unwrapped()) {
            is String -> unwrapped.toBigDecimal()
            is Double -> unwrapped.toBigDecimal()
            is Int -> unwrapped.toBigDecimal()
            else -> throw IllegalArgumentException("Unknown type provided for assets to purchase percentage")
          }
          CryptoCurrency.valueOf(key) to transformedValue
        }
        .toMap()
  }
}

