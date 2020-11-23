package com.example.sim.models

import com.example.sim.api.market.response.MarketResponse

data class CombineOrder(
    val orders: List<MarketResponse>,
    val avgPrice: Float,
    val totalQuantity: Int
)