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
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.example.sim.R
import com.example.sim.api.market.response.MarketResponse
import com.example.sim.models.Profit
import com.example.sim.ui.marketTracker.state.MarketTrackerStateEvent.GetMarketDataByIdEvent
import com.example.sim.util.Constants
import com.example.sim.util.StateMessageCallback
import com.synnapps.carouselview.ViewListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_market_price.*
import kotlinx.android.synthetic.main.item_profit.view.*
import java.text.DecimalFormat

@AndroidEntryPoint
class MarketPriceFragment : BaseMarketTrackerFragment(R.layout.fragment_market_price),
    SwipeRefreshLayout.OnRefreshListener,
    ViewListener {

    lateinit var orderAdapter: MarketOrderAdapter
    lateinit var profits: List<Profit>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        subscribeObservers()
        swipe_refresh.setOnRefreshListener(this)
        setHasOptionsMenu(true)

        viewModel.setStateEvent(GetMarketDataByIdEvent)
    }

    private fun initRecyclerView() {
        orderAdapter = MarketOrderAdapter()
        recycler_view_orders.setHasFixedSize(true)
        recycler_view_orders.adapter = orderAdapter
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { viewState ->

                viewState.marketPriceFields.marketOrderList?.let { marketOrderList ->
                    orderAdapter.submitList(marketOrderList)
                }

                viewState.marketPriceFields.profits?.let { currentProfits ->
                    profits = currentProfits
                    setCurrentProfits()
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

    private fun setCurrentProfits() {
        carousel_view.setViewListener(this)
        if (profits.isEmpty()) {
            carousel_view.pageCount = 1
        } else {
            carousel_view.pageCount = profits.size
        }
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
        val currentQuality = currentProfit.buyAt.combinedOrders[0].quality

        val profitAdapter = ProfitAdapter()
        customView.recycler_view_profit_order.setHasFixedSize(true)
        customView.recycler_view_profit_order.adapter = profitAdapter

        if (currentQuality == 0) {
            customView.rating_bar.visibility = View.GONE
        } else {
            customView.rating_bar.numStars = currentQuality
            customView.rating_bar.rating = currentQuality.toFloat()
        }

        customView.text_view_buy.text = "BUY ${currentProfit.buyAt.combinedOrders.size} ORDERS"
        profitAdapter.submitList(currentProfit.buyAt.combinedOrders)
        customView.chip_cost.text = "Cost ${currentProfit.totalCost.toInt()}"
        customView.chip_profit.text = "Profit ${currentProfit.totalProfit.toInt()}"

        val decimalFormat = DecimalFormat("0.###").format(currentProfit.sellAt)
        customView.chip_sell_at.text = "Sell $decimalFormat"
        return customView
    }

    override fun onRefresh() {
        viewModel.setStateEvent(GetMarketDataByIdEvent)
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearMarketPriceFragment()
    }
}