package com.example.sim.api.market.response

data class MarketResponse(
    val id: Int,
    val kind: Int,
    val quantity: Int,
    val quality: Int,
    val price: Float,
    val seller: Seller,
    val posted: String,
    val fees: Float
){
    data class Seller(
        val id: Int,
        val company: String,
        val logo: String,
    )
}