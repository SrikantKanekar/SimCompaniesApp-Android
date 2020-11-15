package com.example.sim.api.building

import com.example.sim.models.building.Building
import com.example.sim.models.market.Order
import com.example.sim.models.resource.Resource
import retrofit2.http.GET

interface BuildingApiService {

    @GET("v3/en/encyclopedia/resources")
    suspend fun getResources(): List<Resource>

    @GET("v2/buildings/1")
    suspend fun getBuildings(): List<Building>

    @GET("v2/market/79")
    suspend fun getOrders(): List<Order>
}