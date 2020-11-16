package com.example.sim.api.player

import com.example.sim.api.player.response.PlayerResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PlayerApiService {

    @GET("v2/players-by-company/{company}")
    suspend fun getPlayerData(
        @Path("company") companyName: String
    ): PlayerResponse
}