package com.example.sim.ui.resource.state

import com.example.sim.api.resource.responses.ResourceResponse

data class ResourceViewState(
    var resourceResponseList: List<ResourceResponse>? = null
)