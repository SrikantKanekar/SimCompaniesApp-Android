package com.example.sim.ui.building.state

import com.example.sim.util.StateEvent

sealed class BuildingStateEvent : StateEvent {
    object GetAllBuildingsEvent : BuildingStateEvent() {
        override fun errorInfo(): String {
            return "Error Retrieving Buildings"
        }

        override fun toString(): String {
            return "GetAllBuildingsEvent"
        }
    }

    object GetBuildingByKindEvent : BuildingStateEvent() {
        override fun errorInfo(): String {
            return "Error Retrieving Building"
        }

        override fun toString(): String {
            return "GetBuildingByKindEvent"
        }
    }
}