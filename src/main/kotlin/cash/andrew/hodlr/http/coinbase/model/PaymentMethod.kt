package cash.andrew.hodlr.http.coinbase.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentMethod(
        val id: String,
        val type: String,
        val name: String,
        val currency: String
)