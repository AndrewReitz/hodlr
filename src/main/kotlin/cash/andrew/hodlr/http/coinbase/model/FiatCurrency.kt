package cash.andrew.hodlr.http.coinbase.model

import cash.andrew.hodlr.logging.Logger
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * Currencies supported by coinbase.
 * Add more assets as needed. Currently only supporting
 * USD because it's the only one I can run in production.
 */
enum class FiatCurrency {
  USD,
  UNSUPPORTED
}

class FiatCurrencyJsonAdapter(private val logger: Logger) {
  @ToJson
  fun toJson(value: FiatCurrency) = if (value == FiatCurrency.UNSUPPORTED)
    throw IllegalArgumentException("UNSUPPORTED value is not supported by the coinbase api")
  else value.name

  @FromJson
  fun fromJson(value: String) = FiatCurrency.values().firstOrNull { it.name == value }
      ?: FiatCurrency.UNSUPPORTED.also { logger.debug { "Got $value for FiatCurrency, which is currently unsupported" } }
}
