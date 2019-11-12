package cash.andrew.hodlr.http.telegram

import cash.andrew.hodlr.http.telegram.model.BotApiResponse
import cash.andrew.hodlr.util.minutes
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TelegramBotService {

    @GET("/bot{token}/getUpdates")
    suspend fun getUpdates(
        @Path("token") token: String,
        @Query("offset") offset: Long = 0,
        @Query("timeout") timeout: Long = 2.minutes().asSeconds(),
        @Query("allowed_updates") allowedUpdates: String = "message"
    ): BotApiResponse

    @GET("/bot{token}/sendMessage")
    suspend fun sendMessage(
            @Path("token") token: String,
            @Query("chat_id") chatId: Long,
            @Query("text") message: String
    ): Any
}