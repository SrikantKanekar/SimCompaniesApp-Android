package com.example.sim.ui.resource

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.sim.R
import com.example.sim.api.resource.responses.ResourceResponse
import com.example.sim.models.Resource
import com.example.sim.ui.resource.state.ResourceStateEvent
import com.example.sim.ui.resource.state.ResourceStateEvent.*
import com.example.sim.util.StateMessageCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_resources.*

@AndroidEntryPoint
class ResourceFragment : BaseResourceFragment(R.layout.fragment_resources),
    ResourceAdapter.Interaction {

    lateinit var adapter: ResourceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()
        getAllResources()
    }

    private fun getAllResources() {
        if (viewModel.viewState.value == null){
            viewModel.setStateEvent(GetAllResourcesEvent)
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { viewState ->
                viewState.resourceFields.resourcesList?.let { resourceList ->
                    adapter.submitList(resourceList)
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
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

    private fun initRecyclerView() {
        adapter = ResourceAdapter(this)
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = adapter
    }

    override fun onItemSelected(position: Int, item: Resource) {
        viewModel.setResourceId(item.db_letter)
        findNavController().navigate(R.id.action_resourceFragment2_to_viewResourceFragment)
    }
}