package com.example.sim.ui.marketTracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.example.sim.R
import com.example.sim.models.Resource
import com.example.sim.ui.marketTracker.adapters.MarketResourceAdapter
import com.example.sim.ui.marketTracker.adapters.MarketProfitAdapter
import com.example.sim.ui.marketTracker.state.MarketTrackerStateEvent.GetAllResourcesEvent
import com.example.sim.ui.marketTracker.state.MarketTrackerStateEvent.ScanMarketEvent
import com.example.sim.util.Constants.Companion.SORT_COST
import com.example.sim.util.Constants.Companion.SORT_ORDERS
import com.example.sim.util.Constants.Companion.SORT_PROFIT
import com.example.sim.util.Constants.Companion.SORT_QUALITY
import com.example.sim.util.StateMessageCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_market_tracker.*


@AndroidEntryPoint
class MarketTrackerFragment : BaseMarketTrackerFragment(R.layout.fragment_market_tracker),
    MarketResourceAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener,
    MarketProfitAdapter.Interaction {

    lateinit var resourceAdapter: MarketResourceAdapter
    lateinit var profitAdapter: MarketProfitAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        subscribeObservers()
        swipe_refresh.setOnRefreshListener(this)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getAllResources()
        setProfitView()
        swipe_refresh.isEnabled = true
    }

    private fun initRecyclerView() {
        resourceAdapter = MarketResourceAdapter(this)
        recycler_view_resource.setHasFixedSize(true)
        recycler_view_resource.layoutManager = GridLayoutManager(context, 3)
        recycler_view_resource.adapter = resourceAdapter

        profitAdapter = MarketProfitAdapter(this)
        recycler_view_profit.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler_view_profit.adapter = profitAdapter
    }

    private fun getAllResources() {
        if (viewModel.viewState.value?.marketTrackerFields?.resourceList == null) {
            viewModel.setStateEvent(GetAllResourcesEvent)
        }
    }

    private fun setProfitView() {
        if (viewModel.viewState.value?.marketTrackerFields?.profits == null) {
            profitAdapter.showButtonScan()
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { viewState ->
                viewState.marketTrackerFields.resourceList?.let { resourceList ->
                    resourceAdapter.submitList(resourceList)
                }

                viewState.marketTrackerFields.profits?.let { profits ->
                    if (profits.isEmpty()) {
                        profitAdapter.showProfitNotFound()
                    } else {
                        profitAdapter.submitList(profits)
                    }
                }

                viewState.marketTrackerFields.filter?.let { filter ->
                    setProfitsFilter(filter)
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            if (!viewModel.isJobAlreadyActive(ScanMarketEvent)) {
                uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
                recycler_view_resource.scrollToPosition(0)
            }
            progress_bar_inside.visibility = View.INVISIBLE
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

    override fun onItemSelected(position: Int, item: Resource) {
        viewModel.setResource(item)
        val action = MarketTrackerFragmentDirections
            .actionMarketTrackerFragmentToMarketPriceFragment(item.name)
        findNavController().navigate(action)
    }

    override fun scanMarket() {
        profitAdapter.clearList()
        viewModel.setStateEvent(ScanMarketEvent)
        progress_bar_inside.visibility = View.VISIBLE
    }

    private fun setProfitsFilter(filter: String) {

    }

    override fun onRefresh() {
        swipe_refresh.isRefreshing = false
        scanMarket()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort -> {
                showSortDialog()
                true
            }
            else -> {
                item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(
                    item
                )
            }
        }
    }

    private fun showSortDialog() {
        activity?.let { activity ->
            val dialog = MaterialDialog(activity)
                .noAutoDismiss()
                .customView(R.layout.dialog_sort)

            val view = dialog.getCustomView()
            val filter = viewModel.getProfitFilter()

            view.findViewById<RadioGroup>(R.id.sort_group).apply {
                when (filter) {
                    SORT_PROFIT -> check(R.id.sort_profit)
                    SORT_COST -> check(R.id.sort_cost)
                    SORT_QUALITY -> check(R.id.sort_quality)
                    SORT_ORDERS -> check(R.id.sort_orders)
                }
            }

            view.findViewById<TextView>(R.id.positive_button).setOnClickListener {
                val newSort =
                    when (view.findViewById<RadioGroup>(R.id.sort_group).checkedRadioButtonId) {
                        R.id.sort_profit -> SORT_PROFIT
                        R.id.sort_cost -> SORT_COST
                        R.id.sort_quality -> SORT_QUALITY
                        R.id.sort_orders -> SORT_ORDERS
                        else -> SORT_PROFIT
                    }
                viewModel.setProfitFilter(newSort)
//                onBlogSearchOrFilter()
                dialog.dismiss()
            }

            view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun onPause() {
        super.onPause()
        swipe_refresh.isEnabled = false
    }
}