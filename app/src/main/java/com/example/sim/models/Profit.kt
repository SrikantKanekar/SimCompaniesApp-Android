package com.example.sim.models

data class Profit(
    val buyAt: CombineOrder,
    val sellAt: Float,
    val profitValue: Float,
    val cost: Float
)