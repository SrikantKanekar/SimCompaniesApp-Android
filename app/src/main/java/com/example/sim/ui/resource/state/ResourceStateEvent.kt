package com.example.sim.ui.resource.state

import com.example.sim.util.StateEvent

sealed class ResourceStateEvent : StateEvent {
    class ResourceSearchEvent() : ResourceStateEvent() {
        override fun errorInfo(): String {
            return "Error Searching Resources"
        }

        override fun toString(): String {
            return "ResourceSearchEvent"
        }
    }

    class None(): ResourceStateEvent(){
        override fun errorInfo(): String {
            return "None"
        }
    }
}