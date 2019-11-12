package cash.andrew.hodlr.http.coinbase.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    val id: String,
    val base_currency: String,
    val quote_currency: String,
    val base_min_size: String,
    val quote_increment: String
)