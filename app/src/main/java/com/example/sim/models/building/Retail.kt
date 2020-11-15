package com.example.sim.models.building

import com.example.sim.api.resource.responses.ResourceResponse

data class Retail(
    val resourceResponse: ResourceResponse,
    val averagePrice: Float,
    val saturation: Float,
    val retailModeling: String
)