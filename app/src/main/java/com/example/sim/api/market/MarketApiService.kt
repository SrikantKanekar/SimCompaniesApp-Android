package com.example.sim.api.market

import com.example.sim.api.player.response.PlayerResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MarketApiService {

    @GET("v2/market/{id}")
    suspend fun getMarketPrice(
        @Path("db_letter") db_letter: Int
    ): List<PlayerResponse>
}