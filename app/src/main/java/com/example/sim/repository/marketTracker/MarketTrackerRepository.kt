package com.example.sim.repository.marketTracker

import com.example.sim.models.Resource
import com.example.sim.ui.marketTracker.state.MarketTrackerViewState
import com.example.sim.ui.resource.state.ResourceViewState
import com.example.sim.util.DataState
import com.example.sim.util.StateEvent
import kotlinx.coroutines.flow.Flow

interface MarketTrackerRepository {

    fun getAllResources(
        stateEvent: StateEvent
    ): Flow<DataState<MarketTrackerViewState>>

    fun getMarketDataById(
        resource: Resource,
        stateEvent: StateEvent
    ): Flow<DataState<MarketTrackerViewState>>

    fun scanMarket(
        stateEvent: StateEvent
    ): Flow<DataState<MarketTrackerViewState>>
}
