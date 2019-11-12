package cash.andrew.hodlr.moshi

import com.squareup.moshi.Moshi
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BigDecimalAdapterTest {
  @Test
  fun `should be bijective`() {
    val adapter = Moshi.Builder()
        .add(BigDecimal::class.java, BigDecimalAdapter())
        .build()
        .adapter(BigDecimal::class.java)

    val expected = BigDecimal("100.005")
    val json = adapter.toJson(expected)
    val backAgain = adapter.fromJson(json)

    backAgain shouldEqual backAgain
  }
}
