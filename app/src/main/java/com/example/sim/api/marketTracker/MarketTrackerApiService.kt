package com.example.sim.api.marketTracker

import com.example.sim.models.market.Order
import com.example.sim.api.resource.responses.ResourceResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MarketTrackerApiService {

    @GET("v2/market/{id}")
    suspend fun getMarketPrice(
        @Path("id") resourceResponse: ResourceResponse
    ): List<Order>
}