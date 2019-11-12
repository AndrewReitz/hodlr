package cash.andrew.hodlr.util

import java.math.BigDecimal

// since coinbase sends back values with 16 decimal places we will use that as our "coinbase" scale.
private const val COINBASE_SCALE = 16

private fun BigDecimal.withCoinbaseScale(): BigDecimal = setScale(COINBASE_SCALE)

private val ZERO = BigDecimal.ZERO.withCoinbaseScale()

fun BigDecimal.isZero(): Boolean = ZERO == withCoinbaseScale()
fun BigDecimal.isNotZero(): Boolean = !isZero()

// maybe able to switch and use compareTo operation
fun BigDecimal.equalsWithCoinbaseScale(other: BigDecimal): Boolean = withCoinbaseScale() == other.withCoinbaseScale()
