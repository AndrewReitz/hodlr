package cash.andrew.hodlr.util

import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Date
import java.util.concurrent.TimeUnit

/** Force a when statement to be exhaustive without assignment. */
val <T> T.exhaustive: T get() = this

fun ZonedDateTime.toDate(): Date = Date.from(toInstant())

fun LocalDate.monthLength(): Int = month.length(isLeapYear)

fun CharArray.asString() = joinToString(separator = "")

/**
 * Default version of `by lazy` we should use in this project since all
 * cli commands are not threaded.
 */
fun <T> defaultLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)
