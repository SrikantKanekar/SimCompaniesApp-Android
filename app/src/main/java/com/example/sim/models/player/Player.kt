package com.example.sim.models.player

data class Player(
    val id: Int,
    val company: String,
    val logo: String,
    val level: Int,
    val levelName: String,
    val shareValue: Float,
    val shares: Int,
    val note: String,
    val maxBuildings: Int,
    val rank: Int,
    val rating: String,
    val dateJoined: String,
    val lastSeen: String,
    val timezoneOffset: Int,
    val workers: Int,
    val administrationOverhead: Float,
    val productionModifier: Int,
    val salesModifier: Int,
    val recreationBonus: Int,
    val buildings: List<PlayerBuilding>
)