package cash.andrew.hodlr.http

import cash.andrew.hodlr.logging.ConsoleLogger
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KotlinHttpLoggingInterceptor @Inject constructor(
    private val logger: ConsoleLogger
): HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        logger.debug { message }
    }
}
