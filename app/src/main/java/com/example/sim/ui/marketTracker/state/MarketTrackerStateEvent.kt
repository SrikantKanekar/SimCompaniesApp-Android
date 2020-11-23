package com.example.sim.ui.marketTracker.state

import com.example.sim.util.StateEvent

sealed class MarketTrackerStateEvent : StateEvent {
    object GetAllResourcesEvent : MarketTrackerStateEvent() {
        override fun errorInfo(): String {
            return "Error Retrieving Resources"
        }

        override fun toString(): String {
            return "GetAllResourcesEvent"
        }
    }

    object GetMarketDataByIdEvent : MarketTrackerStateEvent() {
        override fun errorInfo(): String {
            return "Failed To Get Market Data"
        }

        override fun toString(): String {
            return "GetMarketDataByIdEvent"
        }
    }
}