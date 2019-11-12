package cash.andrew.hodlr.http.coinbase.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class Account(
    /** Account ID */
    val id: String,
    /** the currency of the account */
    val currency: FiatCurrency,
    /** total funds in the account */
    val balance: BigDecimal,
    /** funds available to withdraw or trade */
    val available: BigDecimal,
    /** funds on hold (not available for use) */
    val hold: BigDecimal,
    @Json(name = "profile_id") val profileId: String
)
