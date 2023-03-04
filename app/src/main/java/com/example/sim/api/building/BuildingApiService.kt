package com.example.sim.api.building

import com.example.sim.api.building.response.BuildingResponse
import retrofit2.http.GET

interface BuildingApiService {

    @GET("v3/0/buildings/0")
    suspend fun getAllBuildings(): List<BuildingResponse>
}