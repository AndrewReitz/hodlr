package cash.andrew.hodlr.logging

interface Logger {
  fun trace(t: Throwable? = null, message: () -> String) {
    log(LogLevel.TRACE, t, message)
  }

  fun debug(t: Throwable? = null, message: () -> String) {
    log(LogLevel.DEBUG, t, message)
  }

  fun info(t: Throwable? = null, message: () -> String) {
    log(LogLevel.INFO, t, message)
  }

  fun warn(t: Throwable? = null, message: () -> String) {
    log(LogLevel.WARN, t, message)
  }

  fun error(t: Throwable? = null, message: () -> String) {
    log(LogLevel.ERROR, t, message)
  }

  fun log(level: LogLevel, t: Throwable?, message: () -> String)
}
