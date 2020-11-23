package com.example.sim.ui.resource

import androidx.hilt.lifecycle.ViewModelInject
import com.example.sim.api.resource.responses.ResourceDetailResponse
import com.example.sim.models.Resource
import com.example.sim.repository.resource.ResourceRepositoryImpl
import com.example.sim.ui.BaseViewModel
import com.example.sim.ui.resource.state.ResourceStateEvent.*
import com.example.sim.ui.resource.state.ResourceViewState
import com.example.sim.util.*
import com.example.sim.util.ErrorHandling.Companion.INVALID_STATE_EVENT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ResourceViewModel @ViewModelInject constructor(
    private val resourceRepository: ResourceRepositoryImpl
) : BaseViewModel<ResourceViewState>() {

    override fun handleNewData(data: ResourceViewState) {
        data.resourceFields.let { resourceFields ->
            resourceFields.resourcesList?.let { resourceList ->
                setResourceListData(resourceList)
            }
        }

        data.viewResourceFields.let { viewResourceFields ->
            viewResourceFields.resourceDetail?.let { resourceDetail ->
                setResourceDetailData(resourceDetail)
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if (!isJobAlreadyActive(stateEvent)) {
            val job: Flow<DataState<ResourceViewState>> =
                when (stateEvent) {
                    is GetAllResourcesEvent -> {
                        resourceRepository.getAllResources(
                            stateEvent = stateEvent
                        )
                    }

                    is GetResourceByIdEvent -> {
                        resourceRepository.getResourceById(
                            id = getResourceId(),
                            stateEvent = stateEvent
                        )
                    }

                    else -> {
                        flow {
                            emit(
                                DataState.error<ResourceViewState>(
                                    response = Response(
                                        message = INVALID_STATE_EVENT,
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

    override fun initNewViewState(): ResourceViewState {
        return ResourceViewState()
    }

    private fun setResourceListData(resourceList: List<Resource>) {
        val update = getCurrentViewStateOrNew()
        update.resourceFields.resourcesList = resourceList
        setViewState(update)
    }

    private fun getResourceId(): Int {
        return getCurrentViewStateOrNew().viewResourceFields.id
            ?: 1
    }

    fun setResourceId(id: Int){
        val update = getCurrentViewStateOrNew()
        update.viewResourceFields.id = id
        setViewState(update)
    }

    private fun setResourceDetailData(resourceDetail: Resource) {
        val update = getCurrentViewStateOrNew()
        update.viewResourceFields.resourceDetail = resourceDetail
        setViewState(update)
    }

    fun clearResourceDetailData() {
        val update = getCurrentViewStateOrNew()
        update.viewResourceFields.resourceDetail = null
        setViewState(update)
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}