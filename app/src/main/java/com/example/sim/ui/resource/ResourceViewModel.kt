package com.example.sim.ui.resource

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.sim.models.building.Building
import com.example.sim.models.market.Order
import com.example.sim.models.resource.Resource
import com.example.sim.repository.resource.ResourceRepository
import com.example.sim.ui.BaseViewModel
import com.example.sim.ui.resource.state.ResourceStateEvent.ResourceSearchEvent
import com.example.sim.ui.resource.state.ResourceViewState
import com.example.sim.util.*
import kotlinx.coroutines.flow.flow

class ResourceViewModel @ViewModelInject constructor(
    private val resourceRepository: ResourceRepository
) : BaseViewModel<ResourceViewState>() {

    fun requestBuildings() {
        resourceRepository.getBuildings()
    }

    fun getBuildings() : LiveData<List<Building>>{
        return resourceRepository.buildings
    }

    fun requestOrders() {
        resourceRepository.getOrders()
    }

    fun getOrders() : LiveData<List<Order>>{
        return resourceRepository.orders
    }

    override fun handleNewData(data: ResourceViewState) {
        data.resourceList?.let { resourceList ->
            setResourceListData(resourceList)
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job = when(stateEvent){
            is ResourceSearchEvent -> {
                resourceRepository.searchResources(stateEvent)
            }

            else -> {
                flow {
                    emit(
                        DataState.error<ResourceViewState>(
                            response = Response(
                                message = null,
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

    override fun initNewViewState(): ResourceViewState {
        return ResourceViewState()
    }

    private fun setResourceListData(resourceList: List<Resource>) {
        val update = getCurrentViewStateOrNew()
        if (update.resourceList == resourceList){
            return
        }
        update.resourceList = resourceList
        setViewState(update)
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}