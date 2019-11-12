package cash.andrew.hodlr.http.telegram

import cash.andrew.hodlr.config.TelegramConfig
import cash.andrew.hodlr.logging.ConsoleLogger
import cash.andrew.hodlr.util.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

interface TelegramBotRepository {
  @ExperimentalCoroutinesApi
  val messages: ReceiveChannel<String>

  suspend fun sendMessage(message: String): Any
}

class DoNothingTelegramBotRepository : TelegramBotRepository {
  @ExperimentalCoroutinesApi
  override val messages: ReceiveChannel<String>
    get() = Channel()

  override suspend fun sendMessage(message: String): Any = Any()
}

class RealTelegramBotRepository(
    telegramConfig: TelegramConfig,
    private val service: TelegramBotService,
    private val logger: ConsoleLogger
) : TelegramBotRepository {

  private val userId = telegramConfig.userId
  private val apiToken = telegramConfig.apiToken

  @ExperimentalCoroutinesApi
  override val messages: ReceiveChannel<String>
    get() = GlobalScope.produce {
      var lastUpdate: Long = 0
      while (true) {
        try {
          service.getUpdates(
              token = apiToken,
              offset = ++lastUpdate
          ).result.forEach {
            lastUpdate = it.updateId
            it.message?.run {
              if (chat.id == userId) {
                send(text)
              }
            }
          }
        } catch (e: Exception) {
          logger.trace(e) { "Error polling bot api" }
          // don't spam the api if there are issues
          1.seconds().delay()
        }
      }
    }

  override suspend fun sendMessage(message: String): SendMessageResponse =
      try {
        service.sendMessage(
            token = apiToken,
            chatId = userId,
            message = message
        )
        SendMessageSuccess
      } catch (e: Exception) {
        SendMessageFailure(e)
      }
}

sealed class SendMessageResponse
object SendMessageSuccess : SendMessageResponse()
data class SendMessageFailure(val exception: Exception) : SendMessageResponse()
