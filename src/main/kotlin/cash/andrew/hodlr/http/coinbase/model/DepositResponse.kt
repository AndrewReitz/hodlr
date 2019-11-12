package cash.andrew.hodlr.http.coinbase.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class DepositResponse(
    val id: String,
    val amount: String,
    val currency: String,
    @Json(name = "payout_at") val payoutAt: Date // todo make zoned date time?
)