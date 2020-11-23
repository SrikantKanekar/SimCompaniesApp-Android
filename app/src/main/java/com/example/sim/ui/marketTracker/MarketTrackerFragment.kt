package com.example.sim.ui.marketTracker

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sim.R
import com.example.sim.models.Resource
import com.example.sim.ui.marketTracker.state.MarketTrackerStateEvent
import com.example.sim.ui.marketTracker.state.MarketTrackerStateEvent.*
import com.example.sim.ui.resource.BaseResourceFragment
import com.example.sim.ui.resource.ResourceAdapter
import com.example.sim.util.StateMessageCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_market_tracker.*

@AndroidEntryPoint
class MarketTrackerFragment : BaseMarketTrackerFragment(R.layout.fragment_market_tracker),
    MarketTrackerResourceAdapter.Interaction {

    lateinit var adapter: MarketTrackerResourceAdapter

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
                viewState.marketTrackerFields.resourceList?.let { resourceList ->
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
        adapter = MarketTrackerResourceAdapter(this)
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = GridLayoutManager(context, 3)
        recycler_view.adapter = adapter
    }

    override fun onItemSelected(position: Int, item: Resource) {
        viewModel.setResource(item)
        val action = MarketTrackerFragmentDirections.actionMarketTrackerFragmentToMarketPriceFragment(item.name)
        findNavController().navigate(action)
    }
}