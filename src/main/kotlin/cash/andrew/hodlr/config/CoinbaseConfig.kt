package cash.andrew.hodlr.config

import cash.andrew.hodlr.util.asString
import okio.ByteString
import okio.ByteString.Companion.decodeBase64

/**
 * All info needed to talk to private coinbase apis
 *
 * Storing as CharArrays so they are not stored in the string pool
 * and significantly harder to accidentally log.
 */
data class CoinbaseConfig(
        val key: CharArray,
        val secret: CharArray,
        val passphrase: CharArray
) {

    constructor(key: String, secret: String, passphrase: String): this(
        key = key.toCharArray(),
        secret = secret.toCharArray(),
        passphrase = passphrase.toCharArray()
    )

    val base64DecodedSecret get(): ByteString = requireNotNull(secret.asString().decodeBase64())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CoinbaseConfig

        if (!key.contentEquals(other.key)) return false
        if (!secret.contentEquals(other.secret)) return false
        if (!passphrase.contentEquals(other.passphrase)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.contentHashCode()
        result = 31 * result + secret.contentHashCode()
        result = 31 * result + passphrase.contentHashCode()
        return result
    }
}
