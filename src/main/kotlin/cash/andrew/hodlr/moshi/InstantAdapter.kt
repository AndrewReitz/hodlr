package cash.andrew.hodlr.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.time.Instant

class InstantAdapter: JsonAdapter<Instant>() {
    override fun toJson(writer: JsonWriter, value: Instant?) {
        writer.value(value?.epochSecond)
    }
    override fun fromJson(reader: JsonReader): Instant = Instant.ofEpochSecond(reader.nextLong())
}