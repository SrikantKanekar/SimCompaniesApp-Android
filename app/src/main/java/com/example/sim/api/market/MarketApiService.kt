package com.example.sim.api.market

import com.example.sim.api.market.response.MarketResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MarketApiService {

    @GET("v2/market/{id}")
    suspend fun getMarketDataById(
        @Path("id") id: Int
    ): List<MarketResponse>
}