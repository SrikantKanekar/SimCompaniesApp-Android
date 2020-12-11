package com.example.sim.models

import com.example.sim.api.market.response.MarketResponse

data class CombinedOrder(
    val combinedOrders: List<MarketResponse>,
    val avgPrice: Float,
    val totalQuantity: Int
) {
    companion object {
        fun dummyCombinedOrder(): CombinedOrder {
            return CombinedOrder(
                combinedOrders = MarketResponse.dummyMarketResponseList(0F),
                avgPrice = 0F,
                totalQuantity = 0
            )
        }
    }
}