package me.mateuspires.tictactoe.data.network

import me.mateuspires.tictactoe.data.models.ImageSearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ImageSearchApi {

    @Headers(
        "X-Mashape-Key: 6WOLO0AnYjmshEl2rfUlWcpB0uXqp1SNNkgjsnX7XtK5U0CoAC",
        "X-Mashape-Host: contextualwebsearch-websearch-v1.p.mashape.com")
    @GET("api/Search/ImageSearchAPI")
    fun search(@Query("q") query: String,
               @Query("count") count: Int = 50,
               @Query("autoCorrect") autoCorrect: Boolean = false)
            : Call<ImageSearch.Result>
}
