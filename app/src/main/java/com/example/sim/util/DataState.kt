package com.example.sim.util

data class DataState<T>(
    var stateMessage: StateMessage? = null,
    var data: T? = null,
    var stateEvent: StateEvent? = null
) {
    companion object {

        fun <T> error(
            response: Response,
            stateEvent: StateEvent?
        ): DataState<T> {
            return DataState(
                stateMessage = StateMessage(response),
                data = null,
                stateEvent = stateEvent
            )
        }

        fun <T> data(
            response: Response?,
            data: T? = null,
            stateEvent: StateEvent?
        ): DataState<T> {
            return DataState(
                stateMessage = response?.let { Response -> StateMessage(Response) },
                data = data,
                stateEvent = stateEvent
            )
        }
    }
}