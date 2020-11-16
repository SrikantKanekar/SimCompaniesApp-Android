package com.example.sim.api.building

import com.example.sim.api.building.response.BuildingResponse
import retrofit2.http.GET

interface BuildingApiService {

    @GET("v2/buildings/1")
    suspend fun getBuildings(): List<BuildingResponse>
}