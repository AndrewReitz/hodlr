package cash.andrew.hodlr.logging

import cash.andrew.hodlr.config.DEFAULT_LOG_LEVEL
import cash.andrew.hodlr.config.LoggingConfig
import cash.andrew.hodlr.http.telegram.TelegramBotRepository
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import javax.inject.Singleton

@Module
class LoggingModule {

  @Provides
  @Singleton
  @ElementsIntoSet
  fun provideLoggers(consoleLogger: ConsoleLogger, telegramLogger: TelegramLogger): Set<@JvmSuppressWildcards Logger> =
      setOf(consoleLogger, telegramLogger)

  @Provides
  @Singleton
  fun provideConsoleLogger(loggingConfig: LoggingConfig, logLevel: LogLevel): ConsoleLogger =
      ConsoleLogger(if (logLevel == DEFAULT_LOG_LEVEL) loggingConfig.logLevel else logLevel)

  @Provides
  @Singleton
  fun provideTelegramLogger(
      telegramBotRepository: TelegramBotRepository,
      loggingConfig: LoggingConfig,
      logLevel: LogLevel
  ): TelegramLogger =
      TelegramLogger(if (logLevel == DEFAULT_LOG_LEVEL) loggingConfig.logLevel else logLevel, telegramBotRepository)

  @Provides
  @Singleton
  fun provideLogger(compositeLogger: CompositeLogger): Logger = compositeLogger
}
