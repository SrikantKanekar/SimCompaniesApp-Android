package com.example.sim.repository.resource

import com.example.sim.ui.resource.state.ResourceViewState
import com.example.sim.util.DataState
import com.example.sim.util.StateEvent
import kotlinx.coroutines.flow.Flow

interface ResourceRepository {

    fun getAllResources(
        stateEvent: StateEvent
    ): Flow<DataState<ResourceViewState>>

    fun getResourceById(
        id: Int,
        stateEvent: StateEvent
    ): Flow<DataState<ResourceViewState>>
}