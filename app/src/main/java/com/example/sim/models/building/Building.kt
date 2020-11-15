package com.example.sim.models.building

data class Building(
    val name: String,
    val image: String,
    val cost: Int,
    val costUnits: Int,
    val steel: Int,
    val wages: Float,
    val secondsToBuild: Int,
    val category: String,
    val kind: String,
    val production: List<Production>?,
    val retail: List<Retail>?
)