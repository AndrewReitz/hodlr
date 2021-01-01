package cash.andrew.hodlr.logging

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class CompositeLoggerTest {

  @Test
  fun `should log to loggers in set`() {
    val testLogger = object : Logger {
        override fun log(level: LogLevel, t: Throwable?, message: () -> String) {
            level shouldBeEqualTo LogLevel.TRACE
            t shouldBe null
            message() shouldBeEqualTo "Should log"
        }
    }
    val classUnderTest = CompositeLogger(setOf(testLogger))

    classUnderTest.trace { "Should log" }


  }
}
