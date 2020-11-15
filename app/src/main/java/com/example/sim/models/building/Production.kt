package com.example.sim.models.building

import com.example.sim.models.resource.Resource

data class Production(
    val resource: Resource,
    val anHour: Float
)