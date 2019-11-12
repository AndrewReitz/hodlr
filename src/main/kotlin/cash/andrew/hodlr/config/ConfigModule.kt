package cash.andrew.hodlr.config

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ConfigModule {

  @Provides
  @Singleton
  fun provideCoinbaseConfig(configContainer: ConfigContainer): CoinbaseConfig = configContainer.coinbaseConfig

  @Provides
  @Singleton
  fun provideLoggingConfig(configContainer: ConfigContainer): LoggingConfig = configContainer.loggingConfig

  @Provides
  @Singleton
  fun provideTelegramConfig(configContainer: ConfigContainer): TelegramConfig? = configContainer.telegramConfig

  @Provides
  @Singleton
  fun provideRecurringPurchase(configContainer: ConfigContainer): RecurringPurchaseConfig =
      configContainer.recurringPurchaseConfig
}