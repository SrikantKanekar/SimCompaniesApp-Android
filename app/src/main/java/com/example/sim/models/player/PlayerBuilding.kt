package com.example.sim.models.player

data class PlayerBuilding(
    val id: Int,
    val kind: String,
    val position: String,
    val image: String,
    val category: String,
    val name: String,
    val cost: Int
)