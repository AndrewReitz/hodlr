package cash.andrew.hodlr.http.telegram

import cash.andrew.hodlr.config.TelegramConfig
import cash.andrew.hodlr.http.telegram.model.BotApiResponse
import cash.andrew.hodlr.http.telegram.model.Chat
import cash.andrew.hodlr.http.telegram.model.Message
import cash.andrew.hodlr.http.telegram.model.Update
import cash.andrew.hodlr.logging.ConsoleLogger
import cash.andrew.hodlr.logging.LogLevel
import cash.andrew.hodlr.stub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.io.IOException
import java.time.Instant

class RealTelegramBotRepositoryTest {

  private val telegramConfig = TelegramConfig(
      userId = 10009,
      apiToken = "000-0000-0000"
  )

  @Test
  @ExperimentalCoroutinesApi
  fun `should produce messages from telegram bot api`() = runBlocking<Unit> {
    val first = listOf(
        Update(
            updateId = 46,
            message = Message(
                messageId = 10,
                chat = Chat(telegramConfig.userId),
                text = "It's off to work we go",
                data = Instant.ofEpochSecond(1000)
            )
        )
    )

    val second = listOf(
        Update(
            updateId = 50,
            message = Message(
                messageId = 11,
                chat = Chat(telegramConfig.userId),
                text = "Say it to me S.A.N.T.O.S.",
                data = Instant.ofEpochSecond(1001)
            )
        ),
        Update(
            updateId = 51,
            message = Message(
                messageId = 12,
                chat = Chat(telegramConfig.userId),
                text = "Heigh-ho, heigh-ho, heigh-ho!",
                data = Instant.ofEpochSecond(1002)
            )
        )
    )

    val args = mutableListOf(
        first,
        second
    )

    var messageOffset = 1L

    val service = object : TelegramBotService by stub() {
      override suspend fun getUpdates(token: String, offset: Long, timeout: Long, allowedUpdates: String): BotApiResponse {
        token shouldBeEqualTo telegramConfig.apiToken
        offset shouldBeEqualTo messageOffset

        val returnVal = args.removeAt(0)

        // next offset should always be 1 greater than the update
        // id we got
        messageOffset = returnVal.last().updateId + 1

        return BotApiResponse(returnVal)
      }
    }

    val classUnderTest = RealTelegramBotRepository(
        telegramConfig = telegramConfig,
        logger = ConsoleLogger(LogLevel.DEBUG),
        service = service
    )

    val messages = classUnderTest.messages
    val result1 = messages.receive()
    val result2 = messages.receive()
    val result3 = messages.receive()

    result1 shouldBeEqualTo first[0].message!!.text
    result2 shouldBeEqualTo second[0].message!!.text
    result3 shouldBeEqualTo second[1].message!!.text
  }

  @Test
  @ExperimentalCoroutinesApi
  fun `should produce messages from telegram bot api and continue on if there is an error`() = runBlocking {
    var messageOffset = 0L

    val service = object : TelegramBotService by stub() {
      override suspend fun getUpdates(token: String, offset: Long, timeout: Long, allowedUpdates: String): BotApiResponse {
        token shouldBeEqualTo telegramConfig.apiToken
        offset shouldBeEqualTo ++messageOffset

        if (messageOffset % 2 == 0L) {
          throw IOException("Oh know!")
        }

        return BotApiResponse(listOf(
            Update(
                updateId = messageOffset,
                message = Message(
                    messageId = 1,
                    chat = Chat(telegramConfig.userId),
                    data = Instant.ofEpochSecond(9001),
                    text = "You will always remember where you were"
                )
            )
        ))
      }
    }

    val classUnderTest = RealTelegramBotRepository(
        telegramConfig = telegramConfig,
        logger = ConsoleLogger(LogLevel.DEBUG),
        service = service
    )

    val messages = classUnderTest.messages
    (1..4).forEach { _ ->
      messages.receive()
    }
  }

  @Test
  fun `should send a message to telegram bot api`() = runBlocking<Unit> {
      val expectedMessage = "This is what space smells like "

      val service = object : TelegramBotService by stub() {
          override suspend fun sendMessage(token: String, chatId: Long, message: String): Any {
              token shouldBeEqualTo telegramConfig.apiToken
              chatId shouldBeEqualTo telegramConfig.userId
              message shouldBeEqualTo expectedMessage
              return Unit
          }
      }

      val classUnderTest = RealTelegramBotRepository(
          telegramConfig = telegramConfig,
          service = service,
          logger = ConsoleLogger(LogLevel.DEBUG)
      )

      val result = classUnderTest.sendMessage(expectedMessage)
      result shouldBeEqualTo SendMessageSuccess
  }

  @Test
  fun `should return an error when send message telegram bot api has an error`() = runBlocking<Unit> {
      val expectedException = IOException("You will always remember where you were")

      val service = object : TelegramBotService by stub() {
          override suspend fun sendMessage(token: String, chatId: Long, message: String): Any {
              throw expectedException
          }
      }

      val classUnderTest = RealTelegramBotRepository(
          telegramConfig = telegramConfig,
          service = service,
          logger = ConsoleLogger(LogLevel.DEBUG)
      )

      val result = classUnderTest.sendMessage("My thoughts are frozen")
      result shouldBeEqualTo SendMessageFailure(expectedException)
  }
}
