package com.example.sim.models.resource

data class Resource(
    val name: String,
    val image: String,
    val db_letter: Int,
    val transportation: Float,
    val retailable: Boolean,
    val research: Boolean
){
    val resourceImageUrl get() = "https://d1fxy698ilbz6u.cloudfront.net/static/$image"
}