package cash.andrew.hodlr.config

import cash.andrew.hodlr.logging.LogLevel
import okhttp3.logging.HttpLoggingInterceptor

val DEFAULT_LOG_LEVEL: LogLevel = LogLevel.INFO

data class LoggingConfig(
    val logLevel: LogLevel = DEFAULT_LOG_LEVEL,
    val httpLogLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE
)
