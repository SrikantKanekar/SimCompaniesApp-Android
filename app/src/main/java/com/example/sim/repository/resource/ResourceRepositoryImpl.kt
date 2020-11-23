package com.example.sim.repository.resource

import android.util.Log
import com.example.sim.api.resource.ResourceApiService
import com.example.sim.api.resource.responses.ResourceDetailResponse
import com.example.sim.api.resource.responses.ResourceResponse
import com.example.sim.models.Resource
import com.example.sim.persistence.ResourceDao
import com.example.sim.repository.NetworkBoundResource
import com.example.sim.ui.resource.state.ResourceViewState
import com.example.sim.ui.resource.state.ResourceViewState.*
import com.example.sim.util.DataState
import com.example.sim.util.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceRepositoryImpl @Inject constructor(
    private val resourceApiService: ResourceApiService,
    private val resourceDao: ResourceDao
) : ResourceRepository {

    private val TAG = "DEBUG"

    override fun getAllResources(stateEvent: StateEvent): Flow<DataState<ResourceViewState>> {
        return object :
            NetworkBoundResource<List<ResourceResponse>, List<Resource>, ResourceViewState>(
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

            override fun handleCacheSuccess(resultObj: List<Resource>): DataState<ResourceViewState> {
                return DataState.data(
                    response = null,
                    data = ResourceViewState(
                        resourceFields = ResourceFields(
                            resourcesList = resultObj
                        )
                    ),
                    stateEvent = stateEvent
                )
            }
        }.result
    }


    override fun getResourceById(
        id: Int,
        stateEvent: StateEvent
    ): Flow<DataState<ResourceViewState>> {
        return object : NetworkBoundResource<ResourceDetailResponse, Resource, ResourceViewState>(
            dispatcher = IO,
            stateEvent = stateEvent,
            apiCall = {
                resourceApiService.getResourceById(id)
            },
            cacheCall = {
                resourceDao.getById(id)
            }
        ) {
            override suspend fun updateCache(resourceDetailResponse: ResourceDetailResponse) {
                withContext(IO) {
                    try {
                        launch {
                            resourceDao.insertOrUpdate(resourceDetailResponse.toResource())
                        }
                    } catch (e: Exception) {
                        Log.e(
                            TAG,
                            "updateLocalDb: error updating cache data on resource with name: ${resourceDetailResponse.name}. " +
                                    "${e.message}"
                        )
                    }
                }
            }

            override fun handleCacheSuccess(resultObj: Resource): DataState<ResourceViewState> {
                return DataState.data(
                    response = null,
                    data = ResourceViewState(
                        viewResourceFields = ViewResourceFields(
                            resourceDetail = resultObj
                        )
                    ),
                    stateEvent = stateEvent
                )
            }
        }.result
    }
}