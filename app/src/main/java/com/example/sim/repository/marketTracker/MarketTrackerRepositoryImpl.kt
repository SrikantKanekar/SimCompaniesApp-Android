package com.example.sim.repository.marketTracker

import android.util.Log
import com.example.sim.api.market.MarketApiService
import com.example.sim.api.market.response.MarketResponse
import com.example.sim.api.resource.ResourceApiService
import com.example.sim.api.resource.responses.ResourceResponse
import com.example.sim.models.Resource
import com.example.sim.persistence.ResourceDao
import com.example.sim.repository.NetworkBoundResource
import com.example.sim.repository.safeApiCall
import com.example.sim.ui.marketTracker.state.MarketTrackerViewState
import com.example.sim.ui.marketTracker.state.MarketTrackerViewState.*
import com.example.sim.ui.resource.state.ResourceViewState
import com.example.sim.util.ApiResponseHandler
import com.example.sim.util.DataState
import com.example.sim.util.MarketTracker
import com.example.sim.util.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketTrackerRepositoryImpl @Inject constructor(
    private val marketApiService: MarketApiService,
    private val resourceApiService: ResourceApiService,
    private val resourceDao: ResourceDao,
) : MarketTrackerRepository {

    private val TAG = "DEBUG"

    override fun getAllResources(stateEvent: StateEvent): Flow<DataState<MarketTrackerViewState>> {
        return object :
            NetworkBoundResource<List<ResourceResponse>, List<Resource>, MarketTrackerViewState>(
                dispatcher = IO,
                stateEvent = stateEvent,
                apiCall = {
                    resourceApiService.getAllResources()
                },
                cacheCall = {
                    resourceDao.getAll()
                }
            ) {
            override suspend fun updateCache(networkObject: List<ResourceResponse>) {
                withContext(IO) {
                    for (resourceResponse in networkObject) {
                        try {
                            launch {
                                resourceDao.insertOrUpdate(resourceResponse.toResource())
                            }
                        } catch (e: Exception) {
                            Log.e(
                                TAG,
                                "updateLocalDb: error updating cache data on resource with name: ${resourceResponse.name}. " +
                                        "${e.message}"
                            )
                        }
                    }
                }
            }

            override fun handleCacheSuccess(resultObj: List<Resource>): DataState<MarketTrackerViewState> {
                return DataState.data(
                    response = null,
                    data = MarketTrackerViewState(
                        marketTrackerFields = MarketTrackerFields(
                            resourceList = resultObj
                        )
                    ),
                    stateEvent = stateEvent
                )
            }
        }.result
    }


    override fun getMarketDataById(
        resource: Resource,
        stateEvent: StateEvent
    ) = flow {
        val apiResult = safeApiCall(IO) {
            marketApiService.getMarketDataById(resource.db_letter)
        }
        emit(
            object : ApiResponseHandler<MarketTrackerViewState, List<MarketResponse>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<MarketResponse>): DataState<MarketTrackerViewState> {
                    val profits = MarketTracker().calculate(resource, resultObj)
                    for (profit in profits){
                        Log.d(TAG, "Profit : ${profit.profitValue}")
                    }
                    return DataState.data(
                        response = null,
                        data = MarketTrackerViewState(
                            marketPriceFields = MarketPriceFields(
                                marketOrderList = resultObj,
                                profits = profits
                            )
                        ),
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )
    }
}
