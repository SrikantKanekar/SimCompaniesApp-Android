package com.example.sim.ui.marketTracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sim.R
import com.example.sim.models.Profit
import com.example.sim.util.Constants.Companion.BUTTON_SCAN_TYPE
import com.example.sim.util.Constants.Companion.NOT_FOUND_TYPE
import com.example.sim.util.Constants.Companion.PROFIT_TYPE
import kotlinx.android.synthetic.main.item_button_scan.view.*
import kotlinx.android.synthetic.main.item_not_found.view.*
import kotlinx.android.synthetic.main.item_profit.view.*
import java.text.DecimalFormat

class MarketProfitAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Profit>() {

        override fun areItemsTheSame(
            oldItem: Profit,
            newItem: Profit
        ): Boolean {
            return oldItem.totalCost == newItem.totalCost
        }

        override fun areContentsTheSame(
            oldItem: Profit,
            newItem: Profit
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position].resource.db_letter) {
            NOT_FOUND_TYPE -> NOT_FOUND_TYPE
            BUTTON_SCAN_TYPE -> BUTTON_SCAN_TYPE
            else -> PROFIT_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            NOT_FOUND_TYPE -> {
                return NotFoundViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_not_found,
                        parent,
                        false
                    )
                )
            }
            BUTTON_SCAN_TYPE -> {
                return ButtonScanViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_button_scan,
                        parent,
                        false
                    ),
                    interaction
                )
            }
            else -> {
                return ProfitViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_profit,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NotFoundViewHolder -> {
                holder.bind(differ.currentList[position])
            }
            is ButtonScanViewHolder -> {
                holder.bind(differ.currentList[position])
            }
            is ProfitViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Profit>) {
        differ.submitList(list)
    }

    fun showProfitNotFound() {
        val dummyProfit = Profit.dummyNotFoundProfit()
        val list = ArrayList<Profit>()
        list.add(dummyProfit)
        submitList(list)
    }

    fun showButtonScan() {
        val dummyProfit = Profit.dummyScanProfit()
        val list = ArrayList<Profit>()
        list.add(dummyProfit)
        submitList(list)
    }

    fun clearList(){
        val list = ArrayList<Profit>()
        submitList(list)
    }

    class ProfitViewHolder
    constructor(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Profit) = with(itemView) {
            val currentQuality = item.buyAt.combinedOrders[0].quality

            val profitOrderAdapter = MarketProfitOrderAdapter()
            itemView.recycler_view_profit_order.setHasFixedSize(true)
            itemView.recycler_view_profit_order.adapter = profitOrderAdapter

            if (currentQuality == 0) {
                itemView.rating_bar.visibility = View.GONE
            } else {
                itemView.rating_bar.numStars = currentQuality
                itemView.rating_bar.rating = currentQuality.toFloat()
            }

            itemView.text_view_buy.text =
                "${item.resource.name}\nBUY ${item.buyAt.combinedOrders.size} ORDERS"
            profitOrderAdapter.submitList(item.buyAt.combinedOrders)
            itemView.chip_cost.text = "Cost ${item.totalCost.toInt()}"
            itemView.chip_profit.text = "Profit ${item.totalProfit.toInt()}"

            val decimalFormat = DecimalFormat("0.###").format(item.sellAt)
            itemView.chip_sell_at.text = "Sell $decimalFormat"
        }
    }

    class NotFoundViewHolder
    constructor(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Profit) = with(itemView) {
            itemView.text_view_not_found.text = "SORRY... NO PROFITS CAN BE GAINED"
        }
    }

    class ButtonScanViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Profit) = with(itemView) {
            itemView.button_scan.setOnClickListener {
                interaction?.scanMarket()
            }
        }
    }

    interface Interaction {
        fun scanMarket()
    }
}
