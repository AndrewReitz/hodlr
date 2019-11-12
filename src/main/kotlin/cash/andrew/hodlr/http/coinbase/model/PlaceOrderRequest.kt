package cash.andrew.hodlr.http.coinbase.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceOrderRequest(
    val type: OrderType = OrderType.market,
    val side: Side = Side.buy,
    @Json(name = "product_id") val productId: String,
    val funds: String
)