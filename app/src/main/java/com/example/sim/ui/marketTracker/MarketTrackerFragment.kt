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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.example.sim.R
import com.example.sim.models.Profit
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
import com.synnapps.carouselview.ViewListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_market_tracker.*
import kotlinx.android.synthetic.main.item_profit.view.*
import java.text.DecimalFormat

@AndroidEntryPoint
class MarketTrackerFragment : BaseMarketTrackerFragment(R.layout.fragment_market_tracker),
    MarketResourceAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener,
    ViewListener {

    lateinit var adapter: MarketResourceAdapter
    lateinit var profits: List<Profit>

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
        setCarouselView()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { viewState ->
                viewState.marketTrackerFields.resourceList?.let { resourceList ->
                    adapter.submitList(resourceList)
                }

                viewState.marketTrackerFields.profits?.let { currentProfits ->
                    profits = currentProfits
                    setCurrentProfits()
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            if (!viewModel.isJobAlreadyActive(ScanMarketEvent)) {
                uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
            }
            recycler_view.scrollToPosition(0)
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
        adapter = MarketResourceAdapter(this)
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = GridLayoutManager(context, 3)
        recycler_view.adapter = adapter
    }


    private fun getAllResources() {
        if (viewModel.viewState.value?.marketTrackerFields?.resourceList == null) {
            viewModel.setStateEvent(GetAllResourcesEvent)
        }
    }

    override fun onItemSelected(position: Int, item: Resource) {
        viewModel.setResource(item)
        val action = MarketTrackerFragmentDirections
            .actionMarketTrackerFragmentToMarketPriceFragment(item.name)
        findNavController().navigate(action)
    }

    private fun setCarouselView() {
        if (viewModel.viewState.value?.marketTrackerFields?.profits == null) {
            button_scan.setOnClickListener {
                scanMarket()
            }
        } else {
            showCarousel()
        }
    }

    private fun scanMarket() {
        viewModel.setStateEvent(ScanMarketEvent)
        button_scan.visibility = View.GONE
        carousel_view.visibility = View.VISIBLE
        progress_bar_inside.visibility = View.VISIBLE
    }

    private fun showCarousel() {
        button_scan.visibility = View.GONE
        carousel_view.visibility = View.VISIBLE
    }

    private fun setCurrentProfits() {
        carousel_view.visibility = View.VISIBLE
        carousel_view.layoutParams.height = 870
        carousel_view.setViewListener(this)
        if (profits.isEmpty()) {
            carousel_view.pageCount = 1
        } else {
            carousel_view.pageCount = profits.size
        }
        progress_bar_inside.visibility = View.INVISIBLE
    }

    override fun setViewForPosition(position: Int): View {
        val customView = layoutInflater.inflate(R.layout.item_profit, null)

        if (profits.isEmpty()) {
            customView.text_view_buy.text = "SORRY... NO PROFITS CAN BE GAINED"
            customView.recycler_view_profit_order.visibility = View.GONE
            customView.chip_group.visibility = View.GONE
            customView.rating_bar.visibility = View.GONE
            return customView
        }

        val currentProfit = profits[position]
        val currentResource = currentProfit.resource
        val currentQuality = currentProfit.buyAt.combinedOrders[0].quality

        val profitAdapter = MarketProfitAdapter()
        customView.recycler_view_profit_order.setHasFixedSize(true)
        customView.recycler_view_profit_order.adapter = profitAdapter

        if (currentQuality == 0) {
            customView.rating_bar.visibility = View.GONE
        } else {
            customView.rating_bar.numStars = currentQuality
            customView.rating_bar.rating = currentQuality.toFloat()
        }

        customView.text_view_buy.text = "${currentResource.name}\nBUY ${currentProfit.buyAt.combinedOrders.size} ORDERS"
        profitAdapter.submitList(currentProfit.buyAt.combinedOrders)
        customView.chip_cost.text = "Cost ${currentProfit.totalCost.toInt()}"
        customView.chip_profit.text = "Profit ${currentProfit.totalProfit.toInt()}"

        val decimalFormat = DecimalFormat("0.###").format(currentProfit.sellAt)
        customView.chip_sell_at.text = "Sell $decimalFormat"
        return customView
    }

    override fun onRefresh() {
        swipe_refresh.isRefreshing = false
        scanMarket()
        carousel_view.visibility = View.INVISIBLE
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
                item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showSortDialog() {
        activity?.let {activity->
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
}