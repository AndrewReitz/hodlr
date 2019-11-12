package cash.andrew.hodlr.http.telegram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant

@JsonClass(generateAdapter = true)
data class Message(
    @Json(name = "message_id") val messageId: Long,
    val chat: Chat,
    val data: Instant?,
    val text: String
)
