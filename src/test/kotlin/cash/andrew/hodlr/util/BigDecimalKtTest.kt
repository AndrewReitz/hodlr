package cash.andrew.hodlr.util

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.math.BigDecimal

// these tests probably aren't necessary
// there are other places that exercise these options
class BigDecimalKtTest {

  @Test
  fun isZero() {
    0.toBigDecimal().isZero() shouldBeEqualTo true
    1.toBigDecimal().isZero() shouldBeEqualTo false
  }

  @Test
  fun isNotZero() {
    0.toBigDecimal().isNotZero() shouldBeEqualTo false
    1.toBigDecimal().isNotZero() shouldBeEqualTo true
  }

  @Test
  fun equalsWithCoinbaseScale() {
    1.0005.toBigDecimal().equalsWithCoinbaseScale(BigDecimal("1.00050000")) shouldBeEqualTo true
    1.5.toBigDecimal().equalsWithCoinbaseScale(BigDecimal("1.4")) shouldBeEqualTo false
  }
}
