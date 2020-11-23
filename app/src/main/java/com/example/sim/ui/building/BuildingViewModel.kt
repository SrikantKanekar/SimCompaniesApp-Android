package com.example.sim.ui.building

import androidx.hilt.lifecycle.ViewModelInject
import com.example.sim.models.Building
import com.example.sim.repository.building.BuildingRepositoryImpl
import com.example.sim.ui.BaseViewModel
import com.example.sim.ui.building.state.BuildingStateEvent
import com.example.sim.ui.building.state.BuildingViewState
import com.example.sim.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BuildingViewModel @ViewModelInject constructor(
    private val buildingRepository: BuildingRepositoryImpl
): BaseViewModel<BuildingViewState>(){

    override fun handleNewData(data: BuildingViewState) {
        data.buildingsFields?.let { buildingsFields ->
            buildingsFields.buildingsList?.let { buildingsList ->
                setBuildingListData(buildingsList)
            }
        }

        data.viewBuildingFields.let { viewBuildingFields ->
            viewBuildingFields.buildingDetail?.let { buildingDetail ->
                setBuildingDetailData(buildingDetail)
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if (!isJobAlreadyActive(stateEvent)) {
            val job: Flow<DataState<BuildingViewState>> =
                when (stateEvent) {
                    is BuildingStateEvent.GetAllBuildingsEvent -> {
                        buildingRepository.getAllBuildings(
                            stateEvent = stateEvent
                        )
                    }

                    is BuildingStateEvent.GetBuildingByKindEvent -> {
                        buildingRepository.getBuildingByKind(
                            id = getBuildingKind(),
                            stateEvent = stateEvent
                        )
                    }

                    else -> {
                        flow {
                            emit(
                                DataState.error<BuildingViewState>(
                                    response = Response(
                                        message = ErrorHandling.INVALID_STATE_EVENT,
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

    override fun initNewViewState(): BuildingViewState {
        return BuildingViewState()
    }

    private fun setBuildingListData(buildingsList: List<Building>) {
        val update = getCurrentViewStateOrNew()
        update.buildingsFields.buildingsList = buildingsList
        setViewState(update)
    }

    private fun setBuildingDetailData(buildingDetail: Building) {
        val update = getCurrentViewStateOrNew()
        update.viewBuildingFields.buildingDetail = buildingDetail
        setViewState(update)
    }

    private fun getBuildingKind(): String {
        return getCurrentViewStateOrNew().viewBuildingFields.kind
            ?: "P"
    }

    fun setBuildingKind(kind: String){
        val update = getCurrentViewStateOrNew()
        update.viewBuildingFields.kind = kind
        setViewState(update)
    }

    fun clearBuildingDetailData(){
        val update = getCurrentViewStateOrNew()
        update.viewBuildingFields.buildingDetail = null
        setViewState(update)
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}