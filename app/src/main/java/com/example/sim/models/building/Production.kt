package com.example.sim.models.building

import com.example.sim.api.resource.responses.ResourceResponse

data class Production(
    val resourceResponse: ResourceResponse,
    val anHour: Float
)