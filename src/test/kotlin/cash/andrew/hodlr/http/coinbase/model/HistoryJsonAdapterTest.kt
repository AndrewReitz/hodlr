package cash.andrew.hodlr.http.coinbase.model

import com.squareup.moshi.Moshi
import org.amshove.kluent.shouldBeEqualTo
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class HistoryJsonAdapterTest {
  @Test
  fun `should be bijective`() {
    @Language("json")
    val expectedJson = "[1415398768, 0.32, 4.2, 0.35, 4.2, 12.3]"

    val moshi = Moshi.Builder()
        .add(HistoryJsonAdapter)
        .build()

    val historicAdapter = moshi.adapter(Historic::class.java)
    val historic = historicAdapter.fromJson(expectedJson)

    val result = historicAdapter.toJson(historic)

    result shouldBeEqualTo expectedJson.replace(" ", "")
  }
}