package cash.andrew.hodlr.http.coinbase.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class PlacedOrderData(
    val id: String,
    val price: String?,
    val funds: String,
    val specified_funds: String,
    val size: String?,
    @Json(name = "product_id") val productId: String,
    val side: Side,
    val type: OrderType,
    @Json(name = "post_only") val postOnly: Boolean,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "fill_fees") val fillFees: String,
    @Json(name = "filled_size") val filledSize: String,
    @Json(name = "executed_value") val executedValue: String,
    val status: String,
    val settled: Boolean
)
