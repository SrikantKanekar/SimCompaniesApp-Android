package com.example.sim.ui.marketTracker

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sim.R
import com.example.sim.api.market.response.MarketResponse
import com.example.sim.models.Profit
import com.example.sim.ui.marketTracker.state.MarketTrackerStateEvent.GetMarketDataByIdEvent
import com.example.sim.util.StateMessageCallback
import com.synnapps.carouselview.ViewListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_market_price.*
import kotlinx.android.synthetic.main.item_profit.view.*
import java.text.DecimalFormat

@AndroidEntryPoint
class MarketPriceFragment : BaseMarketTrackerFragment(R.layout.fragment_market_price),
    MarketOrderAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener,
    ViewListener {

    lateinit var orderAdapter: MarketOrderAdapter
    private var profits: List<Profit>? = null
    private var filteredProfits = ArrayList<Profit>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        subscribeObservers()
        swipe_refresh.setOnRefreshListener(this)
        viewModel.setStateEvent(GetMarketDataByIdEvent)
    }

    private fun initRecyclerView() {
        orderAdapter = MarketOrderAdapter(this)
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
        profits?.let { currentProfits ->
            filteredProfits.clear()
            for (profit in currentProfits) {
                if (profit.profitValue > 0) {
                    if (profit.cost < 1000000) {
                        filteredProfits.add(profit)
                    }
                }
            }
        }

        carousel_view.setViewListener(this)
        if (filteredProfits.isEmpty()) {
            carousel_view.pageCount = 1
        } else {
            carousel_view.pageCount = filteredProfits.size
        }
    }

    override fun onItemSelected(position: Int, item: MarketResponse) {

    }

    override fun onRefresh() {
        clearData()
        viewModel.setStateEvent(GetMarketDataByIdEvent)
    }

    private fun clearData() {
        profits = null
        filteredProfits.clear()
    }

    override fun setViewForPosition(position: Int): View {
        val customView = layoutInflater.inflate(R.layout.item_profit, null)

        if (filteredProfits.isEmpty()) {
            customView.text_view_buy.text = "Sorry... \nNo Results For this Item"
            customView.recycler_view_profit_order.visibility = View.GONE
            customView.chip_group.visibility = View.GONE
            return customView
        }

        val currentProfit = filteredProfits[position]

        val profitAdapter = ProfitAdapter()
        customView.recycler_view_profit_order.setHasFixedSize(true)
        customView.recycler_view_profit_order.adapter = profitAdapter

        customView.text_view_buy.text = "BUY ${currentProfit.buyAt.orders.size} ORDERS"
        profitAdapter.submitList(currentProfit.buyAt.orders)
        customView.chip_cost.text = "Cost ${currentProfit.cost.toInt()}"
        customView.chip_profit.text = "Profit ${currentProfit.profitValue.toInt()}"

        val decimalFormat = DecimalFormat("0.###").format(currentProfit.sellAt)

        customView.chip_sell_at.text = "Sell $decimalFormat"
        return customView
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearMarketPriceFragment()
    }
}