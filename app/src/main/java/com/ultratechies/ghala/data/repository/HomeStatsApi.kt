package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.home.HomeStatsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeStatsApi {

    @GET("stats/{id}")
    suspend fun getStats(
        @Path("id") id: Int
    ): HomeStatsResponse

}