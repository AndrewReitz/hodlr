package cash.andrew.hodlr.http.coinbase.model

import cash.andrew.hodlr.DO_NOTHING_LOGGER
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FiatCurrencyJsonAdapterTest {

  private val classUnderTest = FiatCurrencyJsonAdapter(DO_NOTHING_LOGGER)

  @Test
  fun toJson() {
    classUnderTest.toJson(FiatCurrency.USD) shouldBeEqualTo "USD"

    assertThrows<IllegalArgumentException> {
      classUnderTest.toJson(FiatCurrency.UNSUPPORTED)
    }
  }

  @Test
  fun fromJson() {
    classUnderTest.fromJson("USD") shouldEqual FiatCurrency.USD
    classUnderTest.fromJson("BAT") shouldEqual FiatCurrency.UNSUPPORTED
  }
}
