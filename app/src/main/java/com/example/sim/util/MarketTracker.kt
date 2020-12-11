package com.example.sim.util

import android.util.Log
import com.example.sim.api.market.response.MarketResponse
import com.example.sim.models.CombinedOrder
import com.example.sim.models.Profit
import com.example.sim.models.Resource

private const val TAG = "DEBUG_TRACKER"

class MarketTracker constructor(
    private val resource: Resource,
    private val marketResponse: List<MarketResponse>,
    private val minProfit: Int,
    private val maxCost: Int,
    private val maxQuality: Int,
    private val maxOrders: Int
) {
    var transportCost = 0F

    fun calculate(): List<Profit> {

        transportCost = transportCost(resource)
        Log.d(TAG, "calculate: Transport cost = $transportCost")

        val profitList = ArrayList<Profit>()

        if (marketResponse.isEmpty()) {
            Log.d(TAG, "calculate: MarketResponse is empty. Returning empty List")
            return emptyList()
        }

        val twoDimensionalArray = getTwoDimensionalArray(marketResponse)
        for (array in twoDimensionalArray) {
            for (order in array) {
                Log.d(TAG, "calculate: printing 2D array : ${order.price}")
            }
        }

        for (orders in twoDimensionalArray) {
            val profit = getProfit(orders)
            Log.d(TAG, "calculate: Profits in $orders: size ${profit.size}")
            profitList.addAll(profit)
        }
        return profitList
    }

    private fun getTwoDimensionalArray(orders: List<MarketResponse>): List<List<MarketResponse>> {
        val twoDimensionalArray = ArrayList<ArrayList<MarketResponse>>()
        var currentList = ArrayList<MarketResponse>()

        var quality = orders[0].quality

        for (i in orders.indices) {

            when {

                i == 0 -> {
                    if(quality > maxQuality) {
                        Log.d(TAG, "getTwoDimensionalArray: ----------Breaking------------------")
                        break
                    }
                    currentList.add(orders[i])
                    Log.d(
                        TAG,
                        "getTwoDimensionalArray: Added $i order to currentList ${orders[i].price}"
                    )
                }

                quality == orders[i].quality -> {
                    currentList.add(orders[i])
                    Log.d(
                        TAG,
                        "getTwoDimensionalArray: Added $i order to currentList ${orders[i].price}"
                    )
                }

                quality < orders[i].quality -> {
                    val decrementedPrice = getDecrementedPrice(orders[i].price)

                    if (orders[i-1].price < decrementedPrice){
                        val dummyOrder = MarketResponse.dummyMarketResponse(decrementedPrice)
                        currentList.add(dummyOrder)
                        Log.d(
                            TAG,
                            "getTwoDimensionalArray: Added dummy order to currentList ${dummyOrder.price}"
                        )
                    }

                    twoDimensionalArray.add(currentList)

                    currentList = ArrayList()
                    quality = orders[i].quality
                    if(quality > maxQuality) {
                        Log.d(TAG, "getTwoDimensionalArray: ----------Breaking------------------")
                        break
                    }
                    currentList.add(orders[i])
                    Log.d(
                        TAG,
                        "getTwoDimensionalArray: Added $i order to currentList ${orders[i].price}"
                    )
                }

                i == orders.size - 1 -> {
                    currentList.add(orders[i])
                    twoDimensionalArray.add(currentList)
                    Log.d(
                        TAG,
                        "getTwoDimensionalArray: Added $i order to currentList ${orders[i].price}"
                    )
                    Log.d(
                        TAG,
                        "getTwoDimensionalArray: -----------------------------------------------"
                    )
                }
            }
        }
        return twoDimensionalArray
    }

    private fun getProfit(orders: List<MarketResponse>): List<Profit> {
        val profit = ArrayList<Profit>()

        for (i in 1 until orders.size) {
            val currentOrder = orders[i]
            val combineList = ArrayList<MarketResponse>()

            if (i > maxOrders){
                Log.d(TAG, "getProfit: --------------------Breaking---------------------")
                break
            }

            for (j in 0 until i) {
                combineList.add(orders[j])
                Log.d(TAG, "getProfit: Adding orders to combineList for $i : $j")
            }

            val combinedOrder = combineOrders(combineList)
            Log.d(
                TAG,
                "getProfit: combinedOrder for $i : ${combinedOrder.avgPrice} ${combinedOrder.totalQuantity}"
            )

            val currentProfit = compareOrders(currentOrder, combinedOrder)
            Log.d(TAG, "getProfit: currentProfit for $i ${currentProfit?.totalProfit}")

            if (currentProfit != null) {
                profit.add(currentProfit)
            }
        }
        return profit
    }

    private fun compareOrders(currentOrder: MarketResponse, combinedOrder: CombinedOrder): Profit? {
        val cost = combinedOrder.avgPrice * combinedOrder.totalQuantity
        if (cost > maxCost){
            Log.d(TAG, "compareOrders: Exceeded Cost $cost-------------------------------")
            return null
        }

        var sellAt = currentOrder.price
        if (currentOrder.kind != 1000) {
            sellAt = getDecrementedPrice(currentOrder.price)
        }

        val profitValue =
            sellAt - combinedOrder.avgPrice - transportCost - exchangeCost(currentOrder.price)
        val totalProfit = profitValue * combinedOrder.totalQuantity
        if (totalProfit < minProfit){
            Log.d(TAG, "compareOrders: lower Profit $profitValue-------------------------")
            return null
        }

        return Profit(
            resource = resource,
            buyAt = combinedOrder,
            sellAt = sellAt,
            totalProfit = totalProfit,
            totalCost = cost
        )
    }

    private fun combineOrders(orders: ArrayList<MarketResponse>): CombinedOrder {
        var quantity = 0
        var sum = 0F
        for (order in orders) {
            quantity += order.quantity
            sum += order.quantity * order.price
        }
        val avgPrice = sum / quantity
        return CombinedOrder(orders, avgPrice, quantity)
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
