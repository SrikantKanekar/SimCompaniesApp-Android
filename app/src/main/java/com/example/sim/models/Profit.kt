package com.example.sim.models

data class Profit(
    val resource: Resource,
    val buyAt: CombinedOrder,
    val sellAt: Float,
    val totalCost: Float,
    val totalProfit: Float
)