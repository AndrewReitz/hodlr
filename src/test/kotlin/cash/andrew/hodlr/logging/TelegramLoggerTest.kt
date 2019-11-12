package cash.andrew.hodlr.logging

import cash.andrew.hodlr.http.telegram.TelegramBotRepository
import cash.andrew.hodlr.stub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldContainAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CopyOnWriteArrayList

@ExperimentalCoroutinesApi
class TelegramLoggerTest {

  @Test
  fun `should log messages`() = runBlockingTest {
    val telegramRepository = object : TelegramBotRepository by stub() {
      val receivedMessages: MutableList<String> = CopyOnWriteArrayList()
      override suspend fun sendMessage(message: String): Any {
        receivedMessages.add(message)
        return Unit
      }
    }
    val classUnderTest = TelegramLogger(telegramBotRepository = telegramRepository)

    with(classUnderTest) {
      trace { "trace" }
      debug { "debug" }
      info { "info" }
      warn { "warn" }
      error { "error" }
    }

    with(telegramRepository) {
      receivedMessages shouldContainAll listOf("error", "warn", "info")
    }
  }
}
