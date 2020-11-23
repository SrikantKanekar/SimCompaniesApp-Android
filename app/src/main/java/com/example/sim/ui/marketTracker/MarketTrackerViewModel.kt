package com.example.sim.ui.marketTracker

import androidx.hilt.lifecycle.ViewModelInject
import com.example.sim.api.market.response.MarketResponse
import com.example.sim.models.Profit
import com.example.sim.models.Resource
import com.example.sim.repository.marketTracker.MarketTrackerRepositoryImpl
import com.example.sim.ui.BaseViewModel
import com.example.sim.ui.marketTracker.state.MarketTrackerStateEvent.*
import com.example.sim.ui.marketTracker.state.MarketTrackerViewState
import com.example.sim.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MarketTrackerViewModel @ViewModelInject constructor(
    private val marketTrackerRepository: MarketTrackerRepositoryImpl
) : BaseViewModel<MarketTrackerViewState>() {

    override fun handleNewData(data: MarketTrackerViewState) {
        data.marketPriceFields.let { marketPriceFields ->
            marketPriceFields.marketOrderList?.let { marketOrderList ->
                setMarketOrderListData(marketOrderList)
            }
        }

        data.marketPriceFields.let { marketPriceFields ->
            marketPriceFields.profits?.let { profits ->
                setMarketProfitsData(profits)
            }
        }

        data.marketTrackerFields.let { marketTrackerFields ->
            marketTrackerFields.resourceList?.let { resourceList ->
                setResourceListData(resourceList)
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if (!isJobAlreadyActive(stateEvent)) {
            val job: Flow<DataState<MarketTrackerViewState>> =
                when (stateEvent) {

                    is GetAllResourcesEvent -> {
                        marketTrackerRepository.getAllResources(
                            stateEvent = stateEvent
                        )
                    }

                    is GetMarketDataByIdEvent -> {
                        marketTrackerRepository.getMarketDataById(
                            resource = getResource(),
                            stateEvent = stateEvent
                        )
                    }

                    else -> {
                        flow {
                            emit(
                                DataState.error<MarketTrackerViewState>(
                                    response = Response(
                                        message = ErrorHandling.INVALID_STATE_EVENT,
                                        uiComponentType = UIComponentType.None,
                                        messageType = MessageType.Error
                                    ),
                                    stateEvent = stateEvent
                                )
                            )
                        }
                    }
                }
            launchJob(stateEvent, job)
        }
    }

    override fun initNewViewState(): MarketTrackerViewState {
        return MarketTrackerViewState()
    }

    private fun setResourceListData(resourceList: List<Resource>) {
        val update = getCurrentViewStateOrNew()
        update.marketTrackerFields.resourceList = resourceList
        setViewState(update)
    }

    private fun setMarketOrderListData(marketOrderList: List<MarketResponse>) {
        val update = getCurrentViewStateOrNew()
        update.marketPriceFields.marketOrderList = marketOrderList
        setViewState(update)
    }

    private fun getResource(): Resource {
        return getCurrentViewStateOrNew().marketPriceFields.resource
            ?: Resource(0, "", "", 0F, retailable = false, research = false)
    }

    fun setResource(resource: Resource) {
        val update = getCurrentViewStateOrNew()
        update.marketPriceFields.resource = resource
        setViewState(update)
    }

    private fun setMarketProfitsData(profits: List<Profit>) {
        val update = getCurrentViewStateOrNew()
        update.marketPriceFields.profits = profits
        setViewState(update)
    }

    fun clearMarketPriceFragment() {
        val update = getCurrentViewStateOrNew()
        update.marketPriceFields.marketOrderList = null
        update.marketPriceFields.profits = null
        update.marketPriceFields.resourceId = null
        update.marketPriceFields.resource = null
        setViewState(update)
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}