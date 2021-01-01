package cash.andrew.hodlr.logging

import cash.andrew.hodlr.http.telegram.TelegramBotRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TelegramLogger(
    private val logLevel: LogLevel = LogLevel.INFO,
    private val telegramBotRepository: TelegramBotRepository
): Logger {
  override fun log(level: LogLevel, t: Throwable?, message: () -> String) {
    if (logLevel > level) return

    GlobalScope.launch {
      telegramBotRepository.sendMessage(message())
    }
  }
}
