package cash.andrew.hodlr.util

import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import java.math.BigDecimal

// these tests probably aren't necessary
// there are other places that exercise these options
class BigDecimalKtTest {

  @Test
  fun isZero() {
    0.toBigDecimal().isZero() shouldEqualTo true
    1.toBigDecimal().isZero() shouldEqualTo false
  }

  @Test
  fun isNotZero() {
    0.toBigDecimal().isNotZero() shouldEqualTo false
    1.toBigDecimal().isNotZero() shouldEqualTo true
  }

  @Test
  fun equalsWithCoinbaseScale() {
    1.0005.toBigDecimal().equalsWithCoinbaseScale(BigDecimal("1.00050000")) shouldEqualTo true
    1.5.toBigDecimal().equalsWithCoinbaseScale(BigDecimal("1.4")) shouldEqualTo false
  }
}
