package com.example.sim.repository.resource

import com.example.sim.api.resource.ResourceApiService
import com.example.sim.api.resource.responses.ResourceResponse
import com.example.sim.repository.safeApiCall
import com.example.sim.ui.resource.state.ResourceViewState
import com.example.sim.util.ApiResponseHandler
import com.example.sim.util.DataState
import com.example.sim.util.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceRepository @Inject constructor(private val resourceApiService: ResourceApiService) {

    fun searchResources(
        stateEvent: StateEvent
    ) = flow {
        val apiResult = safeApiCall(IO) {
            resourceApiService.getResources()
        }
        emit(
            object : ApiResponseHandler<ResourceViewState, List<ResourceResponse>>(
                response = apiResult,
                stateEvent = stateEvent
            ){
                override suspend fun handleSuccess(resultObj: List<ResourceResponse>): DataState<ResourceViewState> {
                    return DataState.data(
                        response = null,
                        data = ResourceViewState(resultObj),
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )
    }
}