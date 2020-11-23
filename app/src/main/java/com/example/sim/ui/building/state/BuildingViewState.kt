package com.example.sim.ui.building.state

import com.example.sim.models.Building

data class BuildingViewState(
    var buildingsFields: BuildingsFields = BuildingsFields(),

    var viewBuildingFields: ViewBuildingFields = ViewBuildingFields()
) {
    data class BuildingsFields(
        var buildingsList: List<Building>? = null
    )

    data class ViewBuildingFields(
        var buildingDetail: Building? = null,
        var kind: String? = null
    )
}