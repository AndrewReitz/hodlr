package cash.andrew.hodlr.moshi

import com.squareup.moshi.Moshi
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.time.Instant

class InstantAdapterTest {
    @Test
    fun `should be bijective`() {
        val adapter = Moshi.Builder()
            .add(Instant::class.java, InstantAdapter())
            .build()
            .adapter(Instant::class.java)

        val expected = Instant.ofEpochMilli(1000)
        val json = adapter.toJson(expected)
        val backAgain = adapter.fromJson(json)

        backAgain shouldBeEqualTo backAgain
    }
}
