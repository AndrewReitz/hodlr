package cash.andrew.hodlr.http.telegram.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BotApiResponse(
        val result: List<Update>
)
