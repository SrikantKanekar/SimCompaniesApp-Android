package com.example.sim.repository.marketTracker

import android.content.SharedPreferences
import android.util.Log
import com.example.sim.api.market.MarketApiService
import com.example.sim.api.market.response.MarketResponse
import com.example.sim.api.resource.ResourceApiService
import com.example.sim.api.resource.responses.ResourceResponse
import com.example.sim.models.Profit
import com.example.sim.models.Resource
import com.example.sim.persistence.ResourceDao
import com.example.sim.repository.NetworkBoundResource
import com.example.sim.repository.safeApiCall
import com.example.sim.ui.marketTracker.state.MarketTrackerViewState
import com.example.sim.ui.marketTracker.state.MarketTrackerViewState.MarketPriceFields
import com.example.sim.ui.marketTracker.state.MarketTrackerViewState.MarketTrackerFields
import com.example.sim.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketTrackerRepositoryImpl @Inject constructor(
    private val marketApiService: MarketApiService,
    private val resourceApiService: ResourceApiService,
    private val resourceDao: ResourceDao,
    private val preferences: MyPreferences
) : MarketTrackerRepository {

    private val TAG = "DEBUG_REPO"

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
                    val profits = MarketTracker(
                        resource = resource,
                        marketResponse = resultObj,
                        minProfit = preferences.getMinimumProfit(),
                        maxCost = preferences.getMaximumCost(),
                        maxQuality = preferences.getMaximumQuality(),
                        maxOrders = preferences.getMaximumOrders()
                    ).calculate()

                    for (profit in profits) {
                        Log.d(TAG, "-------------------FINAL PROFIT LIST-----------------")
                        Log.d(
                            TAG,
                            "Profit for ${profit.resource} : ${profit.totalProfit}"
                        )
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

    override fun scanMarket(
        stateEvent: StateEvent
    ) = flow {
        val apiResult = safeApiCall(IO) {
            val profits = ArrayList<Profit>()

            coroutineScope {
                val resources = resourceDao.getAll()
                Log.d(TAG, "scanMarket: resources size ${resources.size}")

                for (resource in resources) {
                    launch {
                        Log.d(TAG, "scanMarket: resource called ${resource.db_letter}")
                        profits.addAll(
                            MarketTracker(
                                resource = resource,
                                marketResponse = marketApiService.getMarketDataById(resource.db_letter),
                                minProfit = preferences.getMinimumProfit(),
                                maxCost = preferences.getMaximumCost(),
                                maxQuality = preferences.getMaximumQuality(),
                                maxOrders = preferences.getMaximumOrders()
                            ).calculate()
                        )
                    }
                }
            }
            Log.d(TAG, "scanMarket: returning-------------------------------------")
            return@safeApiCall profits
        }
        emit(
            object : ApiResponseHandler<MarketTrackerViewState, List<Profit>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<Profit>): DataState<MarketTrackerViewState> {
                    Log.d(TAG, "--------------------FINAL PROFIT LIST----------------------")
                    for (profit in resultObj) {
                        Log.d(
                            TAG,
                            "Profit for ${profit.resource.name}: ${profit.totalProfit} "
                        )
                    }

                    return DataState.data(
                        response = null,
                        data = MarketTrackerViewState(
                            marketTrackerFields = MarketTrackerFields(
                                profits = resultObj
                            )
                        ),
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )
    }
}
