package com.example.sim.ui.building

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.sim.R
import com.example.sim.api.building.response.BuildingResponse
import com.example.sim.models.Building
import com.example.sim.models.Resource
import com.example.sim.ui.building.state.BuildingStateEvent
import com.example.sim.ui.building.state.BuildingStateEvent.*
import com.example.sim.ui.resource.ResourceAdapter
import com.example.sim.ui.resource.ResourceViewModel
import com.example.sim.ui.resource.state.ResourceStateEvent
import com.example.sim.util.StateMessageCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_resources.*

@AndroidEntryPoint
class BuildingFragment : BaseBuildingFragment(R.layout.fragment_building),
    BuildingAdapter.Interaction {

    lateinit var adapter: BuildingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()
        getAllBuildings()
    }

    private fun getAllBuildings() {
        if (viewModel.viewState.value == null){
            viewModel.setStateEvent(GetAllBuildingsEvent)
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { viewState ->
                viewState.buildingsFields.buildingsList?.let { buildingsList ->
                    adapter.submitList(buildingsList)
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
        adapter = BuildingAdapter(this)
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = adapter
    }

    override fun onItemSelected(position: Int, item: Building) {
        viewModel.setBuildingKind(item.kind)
        findNavController().navigate(R.id.action_buildingFragment_to_viewBuildingFragment)
    }
}