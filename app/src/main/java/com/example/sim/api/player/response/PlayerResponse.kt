package com.example.sim.api.player.response

import androidx.room.Entity

data class PlayerResponse(
    val id: Int,
    val company: String,
    val logo: String,
    val level: Int,
    val maxBuildings: Int,
    val rank: Int,
    val timezoneOffset: Int,
    val workers: Int,
    val administrationOverhead: Float,
    val productionModifier: Int,
    val salesModifier: Int,
    val recreationBonus: Int,
    val buildings: List<PlayerBuilding>,
    val message: String
) {
    data class PlayerBuilding(
        val id: Int,
        val kind: String,
        val position: String,
        val category: String,
        val name: String,
        val cost: Int
    )
}