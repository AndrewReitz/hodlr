package cash.andrew.hodlr.http

import cash.andrew.hodlr.config.LoggingConfig
import cash.andrew.hodlr.http.coinbase.model.FiatCurrencyJsonAdapter
import cash.andrew.hodlr.http.coinbase.model.HistoryJsonAdapter
import cash.andrew.hodlr.logging.ConsoleLogger
import cash.andrew.hodlr.moshi.BigDecimalAdapter
import cash.andrew.hodlr.moshi.InstantAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.Date
import javax.inject.Singleton

@Module
class HttpModule {
  @Provides
  @Singleton
  fun providesMoshi(logger: ConsoleLogger): Moshi = Moshi.Builder()
      .add(Date::class.java, Rfc3339DateJsonAdapter())
      .add(Instant::class.java, InstantAdapter())
      .add(BigDecimal::class.java, BigDecimalAdapter())
      .add(FiatCurrencyJsonAdapter(logger))
      .add(HistoryJsonAdapter)
      .build()

  @Provides
  @Singleton
  fun provideOkHttpClient(interceptors: @JvmSuppressWildcards Set<Interceptor>): OkHttpClient = OkHttpClient.Builder()
      .apply { interceptors.forEach { addNetworkInterceptor(it) } }
      .connectTimeout(Duration.ofMinutes(2))
      .build()

  @Provides
  @Singleton
  @IntoSet
  fun provideLoggingInterceptor(
      kotlinLogger: KotlinHttpLoggingInterceptor,
      loggingConfig: LoggingConfig
  ): Interceptor = HttpLoggingInterceptor(kotlinLogger).apply {
    level = loggingConfig.httpLogLevel
  }
}
