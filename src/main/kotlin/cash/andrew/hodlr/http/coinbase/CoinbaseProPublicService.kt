package cash.andrew.hodlr.http.coinbase

import cash.andrew.hodlr.config.CryptoCurrency
import cash.andrew.hodlr.http.coinbase.model.Historic
import cash.andrew.hodlr.http.coinbase.model.Product
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Date

interface CoinbaseProPublicService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/{product-id}/candles")
    suspend fun getHistoricData(
            @Path("product-id") productId: CryptoCurrency,
            @Query("start") start: Date? = null,
            @Query("end") end: Date? = null,
            @Query("granularity") granularity: Int = 60
    ): List<Historic>
}
