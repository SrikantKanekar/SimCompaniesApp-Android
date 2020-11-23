package com.example.sim.repository.building

import android.util.Log
import com.example.sim.api.building.BuildingApiService
import com.example.sim.api.building.response.BuildingResponse
import com.example.sim.models.Building
import com.example.sim.persistence.BuildingDao
import com.example.sim.repository.NetworkBoundResource
import com.example.sim.repository.safeCacheCall
import com.example.sim.ui.building.state.BuildingViewState
import com.example.sim.util.CacheResponseHandler
import com.example.sim.util.DataState
import com.example.sim.util.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuildingRepositoryImpl @Inject constructor(
    private val buildingApiService: BuildingApiService,
    private val buildingDao: BuildingDao
) : BuildingRepository {

    private val TAG = "DEBUG"

    override fun getAllBuildings(stateEvent: StateEvent): Flow<DataState<BuildingViewState>> {
        return object :
            NetworkBoundResource<List<BuildingResponse>, List<Building>, BuildingViewState>(
                dispatcher = IO,
                stateEvent = stateEvent,
                apiCall = {
                    buildingApiService.getAllBuildings()
                },
                cacheCall = {
                    buildingDao.getAll()
                }
            ) {
            override suspend fun updateCache(networkObject: List<BuildingResponse>) {
                withContext(IO) {
                    for (buildingResponse in networkObject) {
                        try {
                            launch {
                                Log.d(
                                    TAG,
                                    "updateLocalDb: inserting buildingResponse: $buildingResponse"
                                )
                                buildingDao.insert(buildingResponse.toBuilding(buildingResponse))
                            }
                        } catch (e: Exception) {
                            Log.e(
                                TAG,
                                "updateLocalDb: error updating cache data on resource with name: ${buildingResponse.name}. " +
                                        "${e.message}"
                            )
                        }
                    }
                }
            }

            override fun handleCacheSuccess(resultObj: List<Building>): DataState<BuildingViewState> {
                return DataState.data(
                    response = null,
                    data = BuildingViewState(
                        buildingsFields = BuildingViewState.BuildingsFields(
                            buildingsList = resultObj
                        )
                    ),
                    stateEvent = stateEvent
                )
            }
        }.result
    }

    override fun getBuildingByKind(
        id: String,
        stateEvent: StateEvent
    ) = flow {
        val cacheResult = safeCacheCall(IO) {
            buildingDao.getByKind(id)
        }
        emit(
            object : CacheResponseHandler<BuildingViewState, Building>(
                response = cacheResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: Building): DataState<BuildingViewState> {
                    return DataState.data(
                        response = null,
                        data = BuildingViewState(
                            viewBuildingFields = BuildingViewState.ViewBuildingFields(
                                buildingDetail = resultObj
                            )
                        ),
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )
    }
}