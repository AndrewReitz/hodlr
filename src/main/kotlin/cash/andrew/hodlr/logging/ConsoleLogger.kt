package cash.andrew.hodlr.logging

import com.github.ajalt.clikt.output.TermUi.echo
import java.time.Clock
import java.time.ZonedDateTime

class ConsoleLogger(
    private val logLevel: LogLevel = LogLevel.INFO,
    private val clock: Clock = Clock.systemDefaultZone()
) : Logger {
  override fun log(level: LogLevel, t: Throwable?, message: () -> String) {
    if (logLevel > level) return

    if (logLevel <= LogLevel.DEBUG) echo("${ZonedDateTime.now(clock)} ${name(message)}: ${message()}", err = level == LogLevel.ERROR)
    else echo(message(), err = level == LogLevel.ERROR)

    t?.let { echo(t, err = level == LogLevel.ERROR) }
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun name(noinline func: () -> String): String {
    val name = func.javaClass.name
    return when {
      name.contains("Kt$") -> name.substringBefore("Kt$")
      name.contains("$") -> name.substringBefore("$")
      else -> name
    }
  }
}
