package cash.andrew.hodlr.http.coinbase.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DepositRequest(
    val amount: Double,
    val currency: FiatCurrency,
    @Json(name = "payment_method_id") val paymentMethodId: String
)