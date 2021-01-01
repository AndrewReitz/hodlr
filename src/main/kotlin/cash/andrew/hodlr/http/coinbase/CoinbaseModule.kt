package cash.andrew.hodlr.http.coinbase

import cash.andrew.hodlr.config.CoinbaseConfig
import cash.andrew.hodlr.config.ConfigContainer
import cash.andrew.hodlr.logging.Logger
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class Coinbase

@Qualifier
annotation class CoinbasePrivate

@Module
class CoinbaseModule {

  @Provides
  @Singleton
  @Coinbase
  fun provideUrl(config: ConfigContainer): HttpUrl =
      if (config.sandbox) "https://api-public.sandbox.pro.coinbase.com/".toHttpUrl()
      else "https://api.pro.coinbase.com/".toHttpUrl()

  @Provides
  @Singleton
  @Coinbase
  fun provideRetrofit(@Coinbase url: HttpUrl, client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
      .client(client)
      .baseUrl(url)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

  @Provides
  @Singleton
  @CoinbasePrivate
  fun providdePriveRetrofit(
      client: OkHttpClient,
      @Coinbase retrofit: Retrofit,
      interceptor: CoinbasePrivateApiRequestHeaderInterceptor
  ): Retrofit {
    val newClient = client.newBuilder()
        .addInterceptor(interceptor)
        .build()

    return retrofit.newBuilder()
        .client(newClient)
        .build()
  }

  @Provides
  @Singleton
  fun provideCoinbaseProService(@Coinbase retrofit: Retrofit): CoinbaseProPublicService =
      retrofit.create(CoinbaseProPublicService::class.java)

  @Provides
  @Singleton
  fun provideCoinbaseProPrivateService(@CoinbasePrivate retrofit: Retrofit): CoinbaseProPrivateService =
      retrofit.create(CoinbaseProPrivateService::class.java)

  @Provides
  @Singleton
  fun provideCoinbasePrivateSignatureGenerator(
      coinbaseConfig: CoinbaseConfig,
      logger: Logger
  ): CoinbasePrivateSignatureGenerator = DefaultCoinbasePrivateSignatureGenerator(
      config = coinbaseConfig,
      logger = logger
  )

  @Provides
  @Singleton
  fun provideCoinbaseRepository(
      coinbaseProPublicService: CoinbaseProPublicService,
      coinbseProPrivateService: CoinbaseProPrivateService
  ): CoinbaseRepository = DefaultCoinbaseRepository(
      publicService = coinbaseProPublicService,
      privateService = coinbseProPrivateService
  )
}
