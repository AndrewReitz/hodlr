package cash.andrew.hodlr

import cash.andrew.hodlr.logging.LogLevel
import cash.andrew.hodlr.logging.Logger
import cash.andrew.hodlr.util.ShutdownHook
import java.lang.reflect.Proxy
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

val DO_NOTHING_SHUTDOWN_HOOK = object : ShutdownHook {
  override fun invoke() {}
}

val DO_NOTHING_LOGGER = object : Logger {
  override fun log(level: LogLevel, t: Throwable?, message: () -> String) = Unit
}

inline fun <reified T : Any> stub(): T =
    Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf<Class<*>>(T::class.java)
    ) { _, _, _ -> TODO() } as T

abstract class TestClock : Clock() {
  override fun instant(): Instant = TODO()
  override fun withZone(zone: ZoneId?): Clock = TODO()
  override fun getZone(): ZoneId = ZoneOffset.UTC
}

abstract class TestAppComponent : AppComponent by stub() {
  override val logger: Logger get() = DO_NOTHING_LOGGER
  override val shutdownHook: ShutdownHook get() = DO_NOTHING_SHUTDOWN_HOOK
}
