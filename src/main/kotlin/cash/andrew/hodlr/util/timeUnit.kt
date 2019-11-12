package cash.andrew.hodlr.util

import java.util.concurrent.TimeUnit

data class TimePlaceHolder(val timeUnit: TimeUnit, val duration: Long) {
  fun asSeconds(): Long = timeUnit.toSeconds(duration)
  suspend inline fun delay() = kotlinx.coroutines.delay(timeUnit.toMillis(duration))
}
fun Int.minutes(): TimePlaceHolder = TimePlaceHolder(TimeUnit.MINUTES, this.toLong())
fun Int.seconds(): TimePlaceHolder = TimePlaceHolder(TimeUnit.SECONDS, this.toLong())
