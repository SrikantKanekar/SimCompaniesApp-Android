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
    private val marketTrackerRepository: MarketTrackerRepositoryImpl,
    private val preferences: MyPreferences
) : BaseViewModel<MarketTrackerViewState>() {

    init {
        getProfitFilter()
    }

    override fun handleNewData(data: MarketTrackerViewState) {
        data.marketTrackerFields.let { marketTrackerFields ->
            marketTrackerFields.resourceList?.let { resourceList ->
                setResourceListData(resourceList)
            }
        }

        data.marketTrackerFields.let { marketTrackerFields ->
            marketTrackerFields.profits?.let { profits ->
                setMarketProfitData(profits)
            }
        }

        data.marketPriceFields.let { marketPriceFields ->
            marketPriceFields.marketOrderList?.let { marketOrderList ->
                setOrderListData(marketOrderList)
            }
        }

        data.marketPriceFields.let { marketPriceFields ->
            marketPriceFields.profits?.let { profits ->
                setResourceProfitData(profits)
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

                    is ScanMarketEvent -> {
                        marketTrackerRepository.scanMarket(
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

    private fun setMarketProfitData(profits: List<Profit>) {
        val update = getCurrentViewStateOrNew()
        update.marketTrackerFields.profits = profits
        setViewState(update)
    }

    private fun setOrderListData(marketOrderList: List<MarketResponse>) {
        val update = getCurrentViewStateOrNew()
        update.marketPriceFields.marketOrderList = marketOrderList
        setViewState(update)
    }

    private fun setResourceProfitData(profits: List<Profit>) {
        val update = getCurrentViewStateOrNew()
        update.marketPriceFields.profits = profits
        setViewState(update)
    }

    fun setResource(resource: Resource) {
        val update = getCurrentViewStateOrNew()
        update.marketPriceFields.resource = resource
        setViewState(update)
    }

    private fun getResource(): Resource {
        return getCurrentViewStateOrNew().marketPriceFields.resource
            ?: Resource.dummyNotFoundResource()
    }

    fun getProfitFilter() : String {
        return preferences.getProfitFilter()
    }

    fun setProfitFilter(filter: String) {
        val update = getCurrentViewStateOrNew()
        update.marketTrackerFields.filter = filter
        update.marketPriceFields.filter = filter
        setViewState(update)
        preferences.setProfitFilter(filter)
    }

    fun clearMarketPriceFragment() {
        val update = getCurrentViewStateOrNew()
        update.marketPriceFields.marketOrderList = null
        update.marketPriceFields.profits = null
        update.marketPriceFields.resource = null
        setViewState(update)
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}