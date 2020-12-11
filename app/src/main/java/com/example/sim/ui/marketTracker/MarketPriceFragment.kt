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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.example.sim.R
import com.example.sim.ui.marketTracker.adapters.MarketOrderAdapter
import com.example.sim.ui.marketTracker.adapters.MarketProfitAdapter
import com.example.sim.ui.marketTracker.state.MarketTrackerStateEvent.GetMarketDataByIdEvent
import com.example.sim.util.Constants
import com.example.sim.util.StateMessageCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_market_price.*
import kotlinx.android.synthetic.main.fragment_market_price.recycler_view_profit
import kotlinx.android.synthetic.main.fragment_market_price.swipe_refresh

@AndroidEntryPoint
class MarketPriceFragment : BaseMarketTrackerFragment(R.layout.fragment_market_price),
    SwipeRefreshLayout.OnRefreshListener {

    lateinit var orderAdapter: MarketOrderAdapter
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
        getMarketDataById()
        swipe_refresh.isEnabled = true
    }

    private fun initRecyclerView() {
        orderAdapter = MarketOrderAdapter()
        recycler_view_orders.setHasFixedSize(true)
        recycler_view_orders.adapter = orderAdapter

        profitAdapter = MarketProfitAdapter()
        recycler_view_profit.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler_view_profit.adapter = profitAdapter
    }

    private fun getMarketDataById() {
        if (viewModel.viewState.value?.marketPriceFields?.marketOrderList == null) {
            viewModel.setStateEvent(GetMarketDataByIdEvent)
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { viewState ->

                viewState.marketPriceFields.marketOrderList?.let { marketOrderList ->
                    orderAdapter.submitList(marketOrderList)
                }

                viewState.marketPriceFields.profits?.let { profits ->
                    if (profits.isEmpty()) {
                        profitAdapter.showProfitNotFound()
                    } else {
                        profitAdapter.submitList(profits)
                    }
                }

                viewState.marketPriceFields.filter?.let { filter ->
                    setProfitsFilter(filter)
                }
                swipe_refresh.isRefreshing = false
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            if (!swipe_refresh.isRefreshing) {
                uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
                recycler_view_orders.scrollToPosition(0)
            }
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

    private fun setProfitsFilter(filter: String) {

    }

    override fun onRefresh() {
        viewModel.setStateEvent(GetMarketDataByIdEvent)
        profitAdapter.clearList()
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
                    Constants.SORT_PROFIT -> check(R.id.sort_profit)
                    Constants.SORT_COST -> check(R.id.sort_cost)
                    Constants.SORT_QUALITY -> check(R.id.sort_quality)
                    Constants.SORT_ORDERS -> check(R.id.sort_orders)
                }
            }

            view.findViewById<TextView>(R.id.positive_button).setOnClickListener {
                val newSort =
                    when (view.findViewById<RadioGroup>(R.id.sort_group).checkedRadioButtonId) {
                        R.id.sort_profit -> Constants.SORT_PROFIT
                        R.id.sort_cost -> Constants.SORT_COST
                        R.id.sort_quality -> Constants.SORT_QUALITY
                        R.id.sort_orders -> Constants.SORT_ORDERS
                        else -> Constants.SORT_PROFIT
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearMarketPriceFragment()
    }
}