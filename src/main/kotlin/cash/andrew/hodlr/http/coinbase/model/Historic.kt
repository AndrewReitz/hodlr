package cash.andrew.hodlr.http.coinbase.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import java.math.BigDecimal
import java.time.Instant

@JsonClass(generateAdapter = true)
data class Historic(
        val time: Instant,
        val low: BigDecimal,
        val high: BigDecimal,
        val open: BigDecimal,
        val close: BigDecimal,
        val volume: BigDecimal
)

@Suppress("unused")
object HistoryJsonAdapter {
    @FromJson fun fromJson(json: List<String>) = Historic(
            time = Instant.ofEpochSecond(json[0].toLong()),
            low = BigDecimal(json[1]),
            high = BigDecimal(json[2]),
            open = BigDecimal(json[3]),
            close = BigDecimal(json[4]),
            volume = BigDecimal(json[5])
    )

    @ToJson fun toJson(value: Historic) = value.run {
        listOf(
            time.epochSecond,
            low.toDouble(),
            high.toDouble(),
            open.toDouble(), close.toDouble(),
            volume.toDouble()
        )
    }
}
