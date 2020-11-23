package com.example.sim.repository.building

import com.example.sim.ui.building.state.BuildingViewState
import com.example.sim.util.DataState
import com.example.sim.util.StateEvent
import kotlinx.coroutines.flow.Flow

interface BuildingRepository {
    fun getAllBuildings(
        stateEvent: StateEvent
    ): Flow<DataState<BuildingViewState>>

    fun getBuildingByKind(
        id: String,
        stateEvent: StateEvent
    ): Flow<DataState<BuildingViewState>>
}