package com.example.sim.models

data class Profit(
    val resource: Resource,
    val buyAt: CombinedOrder,
    val sellAt: Float,
    val totalCost: Float,
    val totalProfit: Float
){
    companion object{
        fun dummyNotFoundProfit(): Profit{
            return Profit(
                resource = Resource.dummyNotFoundResource(),
                buyAt = CombinedOrder.dummyCombinedOrder(),
                sellAt = 0F,
                totalCost = 0F,
                totalProfit = 0F
            )
        }

        fun dummyScanProfit(): Profit{
            return Profit(
                resource = Resource.dummyScanResource(),
                buyAt = CombinedOrder.dummyCombinedOrder(),
                sellAt = 0F,
                totalCost = 0F,
                totalProfit = 0F
            )
        }
    }
}