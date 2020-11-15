package com.example.sim.models.market

data class Order(
    val id: Int,
    val kind: Int,
    val quantity: Int,
    val quality: Int,
    val price: Float,
    val seller: Seller,
    val posted: String,
    val fees: Float
)