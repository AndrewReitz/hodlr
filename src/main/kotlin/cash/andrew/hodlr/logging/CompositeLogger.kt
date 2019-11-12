package cash.andrew.hodlr.logging

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Logger than sends logs to other loggers.
 */
@Singleton
class CompositeLogger @Inject constructor(private val loggers: Set<@JvmSuppressWildcards Logger>): Logger {
  override fun log(level: LogLevel, t: Throwable?, message: () -> String) {
    loggers.forEach { it.log(level, t, message) }
  }
}
