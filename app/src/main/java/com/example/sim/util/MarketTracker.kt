package com.example.sim.util

import android.util.Log
import com.example.sim.api.market.response.MarketResponse
import com.example.sim.models.CombineOrder
import com.example.sim.models.Profit
import com.example.sim.models.Resource

private const val TAG = "DEBUG_TRACKER"

class MarketTracker {

    var transportCost = 0F

    fun calculate(
        resource: Resource,
        marketResponse: List<MarketResponse>
    ): List<Profit> {

        transportCost = transportCost(resource)
        Log.d(TAG, "calculate: Transport cost = $transportCost")

        val profitList = ArrayList<Profit>()

        if (marketResponse.isEmpty()){
            return emptyList()
        }

        val twoDimensionalArray = getTwoDimensionalArray(marketResponse)
        for (array in twoDimensionalArray){
            for (order in array){
                Log.d(TAG, "calculate: printing 2D array : ${order.price}")
            }
        }

        for (orders in twoDimensionalArray) {
            val profit = getProfit(orders)
            profitList.addAll(profit)
        }
        return profitList
    }

    private fun getTwoDimensionalArray(orders: List<MarketResponse>): List<List<MarketResponse>> {
        val twoDimensionalArray = ArrayList<ArrayList<MarketResponse>>()
        var currentList = ArrayList<MarketResponse>()

        var quality = orders[0].quality
        Log.d(TAG, "getTwoDimensionalArray: quality of first order : $quality")

        for (i in orders.indices) {

            when {

                i == 0 -> {
                    currentList.add(orders[i])
                    Log.d(TAG, "getTwoDimensionalArray: Added $i order to currentList ${orders[i].price}")
                }

                i == orders.size-1 -> {
                    currentList.add(orders[i])
                    twoDimensionalArray.add(currentList)
                    Log.d(TAG, "getTwoDimensionalArray: Added $i order to currentList ${orders[i].price}\n " +
                            "------------------------------------------------------------------------")
                }

                quality < orders[i].quality -> {
                    val dummyOrder = MarketResponse(
                        0,
                        1000,
                        0,
                        0,
                        getDecrementedPrice(orders[i].price),
                        MarketResponse.Seller(0, "", ""),
                        "",
                        0F
                    )
                    currentList.add(dummyOrder)
                    Log.d(TAG, "getTwoDimensionalArray: Added dummy order to currentList ${dummyOrder.price}")

                    twoDimensionalArray.add(currentList)

                    currentList = ArrayList()
                    quality = orders[i].quality

                    currentList.add(orders[i])
                    Log.d(TAG, "getTwoDimensionalArray: Added $i order to currentList ${orders[i].price}")
                }

                quality == orders[i].quality -> {
                    currentList.add(orders[i])
                    Log.d(TAG, "getTwoDimensionalArray: Added $i order to currentList ${orders[i].price}")
                }
            }
        }
        return twoDimensionalArray
    }

    private fun getProfit(orders: List<MarketResponse>): List<Profit> {
        val profit = ArrayList<Profit>()

        for (i in orders.indices) {
            val currentOrder = orders[i]

            val combineList = ArrayList<MarketResponse>()
            for (j in 0 until i) {
                combineList.add(orders[j])
                Log.d(TAG, "getProfit: Adding orders to combineList for $i : $j")
            }

            val combinedOrder = combineOrders(combineList)
            Log.d(TAG, "getProfit: combinedOrder for $i : ${combinedOrder.avgPrice} ${combinedOrder.totalQuantity}")

            val currentProfit = compareOrders(currentOrder, combinedOrder)
            Log.d(TAG, "getProfit: currentProfit for $i ${currentProfit.profitValue}")

            if (currentProfit.profitValue > 0F) {
                profit.add(currentProfit)
            }
        }
        return profit
    }

    private fun compareOrders(currentOrder: MarketResponse, combinedOrder: CombineOrder): Profit {
        var sellAt = currentOrder.price

        if (currentOrder.kind != 1000){
            sellAt -= getDecrementedPrice(currentOrder.price)
        }

        val profitValue = sellAt - combinedOrder.avgPrice - transportCost - exchangeCost(currentOrder.price)

        val cost = combinedOrder.avgPrice * combinedOrder.totalQuantity

        return Profit(
            buyAt = combinedOrder,
            sellAt = sellAt,
            profitValue = (profitValue * combinedOrder.totalQuantity),
            cost = cost
        )
    }

    private fun combineOrders(orders: ArrayList<MarketResponse>): CombineOrder {
        var quantity = 0
        var sum = 0F

        for (order in orders) {
            quantity += order.quantity
            sum += order.quantity * order.price
        }
        val avgPrice = sum / quantity

        return CombineOrder(orders, avgPrice, quantity)
    }

    private fun getDecrementedPrice(price: Float): Float {
        return price - priceIncrement(price)
    }

    private fun priceIncrement(price: Float): Float {
        return when {
            price < 0.5 -> 0.001F
            price < 1 -> 0.005F
            price < 2 -> 0.01F
            price < 5 -> 0.05F
            price < 20 -> 0.1F
            price < 50 -> 0.25F
            price < 100 -> 0.5F
            price < 200 -> 1F
            price < 500 -> 2F
            price < 1000 -> 5F
            price < 5000 -> 10F
            price < 10000 -> 25F
            price < 15000 -> 100F
            else -> 0F
        }
    }

    private fun transportCost(resource: Resource): Float {
        return (resource.transportation * Constants.TRANSPORT_COST)
    }

    private fun exchangeCost(price: Float): Float {
        return 0.03F * price
    }
}
