package cash.andrew.hodlr.util

import okhttp3.OkHttpClient

/** Shut down OkHttpClient resources so that the JVM can exit cleanly. */
fun OkHttpClient.shutDown() {
  dispatcher.executorService.shutdown()
  connectionPool.evictAll()
}
