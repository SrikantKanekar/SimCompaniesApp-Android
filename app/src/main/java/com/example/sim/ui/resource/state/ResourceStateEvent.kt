package com.example.sim.ui.resource.state

import com.example.sim.util.StateEvent

sealed class ResourceStateEvent : StateEvent {
    object GetAllResourcesEvent : ResourceStateEvent() {
        override fun errorInfo(): String {
            return "Error Retrieving Resources"
        }

        override fun toString(): String {
            return "GetAllResourcesEvent"
        }
    }

    object GetResourceByIdEvent: ResourceStateEvent(){
        override fun errorInfo(): String {
            return "Error Retrieving Resource"
        }

        override fun toString(): String {
            return "GetResourceByIdEvent"
        }
    }

    object None : ResourceStateEvent() {
        override fun errorInfo(): String {
            return "None"
        }
    }
}