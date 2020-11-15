package com.example.sim.repository.resource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sim.api.resource.ResourceApiService
import com.example.sim.models.building.Building
import com.example.sim.models.market.Order
import com.example.sim.models.resource.Resource
import com.example.sim.repository.safeApiCall
import com.example.sim.ui.resource.state.ResourceViewState
import com.example.sim.util.ApiResponseHandler
import com.example.sim.util.DataState
import com.example.sim.util.StateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceRepository @Inject constructor(private val resourceApiService: ResourceApiService) {
    private val TAG = "Repository"

    fun searchResources(
        stateEvent: StateEvent
    ) = flow {
        val apiResult = safeApiCall(IO) {
            resourceApiService.getResources()
        }
        emit(
            object : ApiResponseHandler<ResourceViewState, List<Resource>>(
                response = apiResult,
                stateEvent = stateEvent
            ){
                override suspend fun handleSuccess(resultObj: List<Resource>): DataState<ResourceViewState> {
                    return DataState.data(
                        response = null,
                        data = ResourceViewState(resultObj),
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )
    }

    private var _buildings: MutableLiveData<List<Building>> = MutableLiveData()
    val buildings: LiveData<List<Building>>
        get() = _buildings

    fun getBuildings() {
        CoroutineScope(IO).launch {
            val update = resourceApiService.getBuildings()
            _buildings.postValue(update)
        }
    }

    private var _orders: MutableLiveData<List<Order>> = MutableLiveData()
    val orders: LiveData<List<Order>>
        get() = _orders

    fun getOrders() {
        CoroutineScope(IO).launch {
            val update = resourceApiService.getOrders()
            _orders.postValue(update)
        }
    }
}