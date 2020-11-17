package com.example.sim.ui.resource

import androidx.hilt.lifecycle.ViewModelInject
import com.example.sim.api.resource.responses.ResourceResponse
import com.example.sim.repository.ResourceRepository
import com.example.sim.ui.BaseViewModel
import com.example.sim.ui.resource.state.ResourceStateEvent.ResourceSearchEvent
import com.example.sim.ui.resource.state.ResourceViewState
import com.example.sim.util.*
import kotlinx.coroutines.flow.flow

class ResourceViewModel @ViewModelInject constructor(
    private val resourceRepository: ResourceRepository
) : BaseViewModel<ResourceViewState>() {

    override fun handleNewData(data: ResourceViewState) {
        data.resourceResponseList?.let { resourceList ->
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

    private fun setResourceListData(resourceResponseList: List<ResourceResponse>) {
        val update = getCurrentViewStateOrNew()
        if (update.resourceResponseList == resourceResponseList){
            return
        }
        update.resourceResponseList = resourceResponseList
        setViewState(update)
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}