package com.example.sim.ui.resource

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.example.sim.R
import com.example.sim.api.resource.responses.ResourceResponse
import com.example.sim.ui.resource.state.ResourceStateEvent
import com.example.sim.util.StateMessageCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_resources.*

@AndroidEntryPoint
class ResourceFragment : BaseResourceFragment(R.layout.fragment_resources), ResourceAdapter.Interaction {

    lateinit var adapter: ResourceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ResourceAdapter(this)

        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = adapter

        subscribeObservers()

        viewModel.setStateEvent(ResourceStateEvent.ResourceSearchEvent())
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.resourceResponseList?.let { list ->
                adapter.submitList(list)
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            stateMessage?.let {
                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        })
    }

    override fun onItemSelected(position: Int, item: ResourceResponse) {

    }
}