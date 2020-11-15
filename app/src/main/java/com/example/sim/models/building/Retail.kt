package com.example.sim.models.building

import com.example.sim.models.resource.Resource

data class Retail(
    val resource: Resource,
    val averagePrice: Float,
    val saturation: Float,
    val retailModeling: String
)