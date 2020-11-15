package com.example.sim.models.resource

data class ResourceDetail(
    val name: String,
    val image: String,
    val db_letter: Int,
    val transportation: Float,
    val retailable: Boolean,
    val research: Boolean,
    val producedFrom: List<ProducedFrom>,
    val soldAt: List<SoldAt>,
    val producedAt: ProducedAt,
    val neededFor: List<Resource>,
    val retailData: List<RetailData>,
    val improvesQualityOf: List<Resource>

){
    val resourceImageUrl get() = "https://d1fxy698ilbz6u.cloudfront.net/static/$image"
}