package com.example.sim.repository

import com.example.sim.util.*
import com.example.sim.util.ApiResult.*
import com.example.sim.util.ErrorHandling.Companion.NETWORK_ERROR
import com.example.sim.util.ErrorHandling.Companion.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class NetworkBoundResource<NetworkObj, CacheObj, ViewState>
constructor(
    private val dispatcher: CoroutineDispatcher,
    private val stateEvent: StateEvent,
    private val apiCall: suspend () -> NetworkObj?,
    private val cacheCall: suspend () -> CacheObj?
) {
    val result: Flow<DataState<ViewState>> = flow {

        // ****** STEP 1: VIEW CACHE ******
        emit(returnCache(markJobComplete = false))

        // ****** STEP 2: MAKE NETWORK CALL, SAVE RESULT TO CACHE ******
        val apiResult = safeApiCall(dispatcher) { apiCall.invoke() }

        when (apiResult) {
            is GenericError -> {
                emit(
                    buildError<ViewState>(
                        apiResult.errorMessage?.let { it } ?: UNKNOWN_ERROR,
                        UIComponentType.Toast,
                        stateEvent
                    )
                )
            }

            is NetworkError -> {
                emit(
                    buildError<ViewState>(
                        NETWORK_ERROR,
                        UIComponentType.None,
                        stateEvent
                    )
                )
            }

            is Success -> {
                if (apiResult.value == null) {
                    emit(
                        buildError<ViewState>(
                            UNKNOWN_ERROR,
                            UIComponentType.None,
                            stateEvent
                        )
                    )
                } else {
                    updateCache(apiResult.value as NetworkObj)
                }
            }
        }

        // ****** STEP 3: VIEW CACHE and MARK JOB COMPLETED ******
        emit(returnCache(markJobComplete = true))
    }

    private suspend fun returnCache(markJobComplete: Boolean): DataState<ViewState> {

        val cacheResult = safeCacheCall(dispatcher) { cacheCall.invoke() }

        var jobCompleteMarker: StateEvent? = null
        if (markJobComplete) {
            jobCompleteMarker = stateEvent
        }

        return object : CacheResponseHandler<ViewState, CacheObj>(
            response = cacheResult,
            stateEvent = jobCompleteMarker
        ) {
            override suspend fun handleSuccess(resultObj: CacheObj): DataState<ViewState> {
                return handleCacheSuccess(resultObj)
            }
        }.getResult()
    }

    abstract suspend fun updateCache(networkObject: NetworkObj)

    abstract fun handleCacheSuccess(resultObj: CacheObj): DataState<ViewState> // make sure to return null for stateEvent

}