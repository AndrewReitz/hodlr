package cash.andrew.hodlr.logging

import cash.andrew.hodlr.OutputStreamTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.lang.NullPointerException
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ConsoleLoggerTest : OutputStreamTest() {

  @Test
  fun trace() {
    val classUnderTest = ConsoleLogger(
        logLevel = LogLevel.TRACE,
        clock = Clock.fixed(Instant.ofEpochMilli(1000), ZoneOffset.UTC)
    )
    classUnderTest.trace { "Trace" }
    classUnderTest.debug { "Debug" }
    classUnderTest.info { "Info" }
    classUnderTest.warn { "Warn" }
    classUnderTest.error { "Error" }

    outputText shouldBeEqualTo """
      1970-01-01T00:00:01Z cash.andrew.hodlr.logging.ConsoleLoggerTest: Trace
      1970-01-01T00:00:01Z cash.andrew.hodlr.logging.ConsoleLoggerTest: Debug
      1970-01-01T00:00:01Z cash.andrew.hodlr.logging.ConsoleLoggerTest: Info
      1970-01-01T00:00:01Z cash.andrew.hodlr.logging.ConsoleLoggerTest: Warn
    """.trimIndent()
    errorText shouldBeEqualTo "1970-01-01T00:00:01Z cash.andrew.hodlr.logging.ConsoleLoggerTest: Error"
  }

  @Test
  fun debug() {
    val classUnderTest = ConsoleLogger(
        logLevel = LogLevel.DEBUG,
        clock = Clock.fixed(Instant.ofEpochMilli(1000), ZoneOffset.UTC)
    )
    classUnderTest.trace { "Trace" }
    classUnderTest.debug { "Debug" }
    classUnderTest.info { "Info" }
    classUnderTest.warn { "Warn" }
    classUnderTest.error { "Error" }

    outputText shouldBeEqualTo """
      1970-01-01T00:00:01Z cash.andrew.hodlr.logging.ConsoleLoggerTest: Debug
      1970-01-01T00:00:01Z cash.andrew.hodlr.logging.ConsoleLoggerTest: Info
      1970-01-01T00:00:01Z cash.andrew.hodlr.logging.ConsoleLoggerTest: Warn
    """.trimIndent()

    errorText shouldBeEqualTo "1970-01-01T00:00:01Z cash.andrew.hodlr.logging.ConsoleLoggerTest: Error"
  }

  @Test
  fun info() {
    val classUnderTest = ConsoleLogger(LogLevel.INFO)
    classUnderTest.trace { "Trace" }
    classUnderTest.debug { "Debug" }
    classUnderTest.info { "Info" }
    classUnderTest.warn { "Warn" }
    classUnderTest.error { "Error" }

    outputText shouldBeEqualTo """
      Info
      Warn
    """.trimIndent()

    errorText shouldBeEqualTo "Error"
  }

  @Test
  fun warn() {
    val classUnderTest = ConsoleLogger(LogLevel.WARN)
    classUnderTest.trace { "Trace" }
    classUnderTest.debug { "Debug" }
    classUnderTest.info { "Info" }
    classUnderTest.warn { "Warn" }
    classUnderTest.error { "Error" }

    outputText shouldBeEqualTo "Warn"
    errorText shouldBeEqualTo "Error"
  }

  @Test
  fun error() {
    val classUnderTest = ConsoleLogger(LogLevel.ERROR)
    classUnderTest.trace { "Trace" }
    classUnderTest.debug { "Debug" }
    classUnderTest.info { "Info" }
    classUnderTest.warn { "Warn" }
    classUnderTest.error { "Error" }

    outputText shouldBeEqualTo ""
    errorText shouldBeEqualTo "Error"
  }

  @Test
  fun `should log exception error level`() {
    val classUnderTest = ConsoleLogger(LogLevel.ERROR)
    classUnderTest.error(NullPointerException("No No!")) { "There was an error" }

    outputText shouldBeEqualTo ""
    errorText shouldBeEqualTo "There was an error\njava.lang.NullPointerException: No No!"
  }

  @Test
  fun `should log exception debug level`() {
    val classUnderTest = ConsoleLogger(LogLevel.TRACE, clock = Clock.fixed(Instant.ofEpochMilli(1000), ZoneOffset.UTC))
    classUnderTest.debug(NullPointerException("No No!")) { "There was an error" }

    outputText shouldBeEqualTo """
      1970-01-01T00:00:01Z cash.andrew.hodlr.logging.ConsoleLoggerTest: There was an error
      java.lang.NullPointerException: No No!
    """.trimIndent()
    errorText shouldBeEqualTo ""
  }
}
