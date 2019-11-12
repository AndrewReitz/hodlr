package cash.andrew.hodlr.util

import com.typesafe.config.Config

fun <T> Config.maybeConfig(key: String, configurator: Config.() -> T): T? =
    if (hasPath(key)) configurator(getConfig(key)) else null

inline fun <reified T : Enum<T>> Config.getEnum(key: String): T = getEnum(T::class.java, key)

inline fun <reified T : Enum<T>> Config.getEnumOrDefault(key: String, defaultValue: T): T =
    if (hasPath(key)) getEnum(T::class.java, key) else defaultValue

fun Config.getBooleanOrDefault(key: String, defaultValue: Boolean): Boolean =
    if (hasPath(key)) getBoolean(key) else defaultValue

fun Config.getIntOrDefault(key: String, defaultValue: Int): Int =
    if (hasPath(key)) getInt(key) else defaultValue

fun Config.getStringOrDefault(key: String, defaultValue: String): String =
    if (hasPath(key)) getString(key) else defaultValue
