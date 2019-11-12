package cash.andrew.hodlr.http.telegram.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Update(
    @Json(name = "update_id") val updateId: Long,
    val message: Message?
)
