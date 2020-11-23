package com.example.sim.ui.resource.state

import com.example.sim.models.Resource

data class ResourceViewState(
    var resourceFields: ResourceFields = ResourceFields(),

    var viewResourceFields: ViewResourceFields = ViewResourceFields()

) {
    data class ResourceFields(
        var resourcesList: List<Resource>? = null
    )

    data class ViewResourceFields(
        var resourceDetail: Resource? = null,
        var id: Int? = null
    )
}