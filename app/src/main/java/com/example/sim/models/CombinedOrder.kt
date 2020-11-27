package com.example.sim.models

import com.example.sim.api.market.response.MarketResponse

data class CombinedOrder(
    val combinedOrders: List<MarketResponse>,
    val avgPrice: Float,
    val totalQuantity: Int
)