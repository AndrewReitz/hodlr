package cash.andrew.hodlr.http.telegram

import cash.andrew.hodlr.config.TelegramConfig
import cash.andrew.hodlr.logging.ConsoleLogger
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class Telegram

@Module
class TelegramModule {

  @Provides
  @Singleton
  @Telegram
  fun provideTelegram(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
      .client(client)
      .baseUrl("https://api.telegram.org")
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

  @Provides
  @Singleton
  fun provideTelegramBotService(@Telegram retrofit: Retrofit): TelegramBotService =
      retrofit.create(TelegramBotService::class.java)

  @Provides
  @Singleton
  fun provideTelegramRepository(
      telegramConfig: TelegramConfig?,
      telegramBotService: TelegramBotService,
      logger: ConsoleLogger
  ): TelegramBotRepository {
    if (telegramConfig == null) {
      return DoNothingTelegramBotRepository()
    }

    return RealTelegramBotRepository(
        telegramConfig = telegramConfig,
        service = telegramBotService,
        logger = logger
    )
  }
}
