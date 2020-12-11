package com.example.sim.ui.marketTracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sim.R
import com.example.sim.api.market.response.MarketResponse
import kotlinx.android.synthetic.main.item_profit_order.view.*
import java.text.DecimalFormat

class MarketProfitOrderAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MarketResponse>() {

        override fun areItemsTheSame(
            oldItem: MarketResponse,
            newItem: MarketResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MarketResponse,
            newItem: MarketResponse
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ProfitOrderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_profit_order,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProfitOrderViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<MarketResponse>) {
        differ.submitList(list)
    }

    class ProfitOrderViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: MarketResponse) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            Glide.with(itemView)
                .load(item.seller.logo)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.default_profile)
                .into(image_view_order)
            text_view_order_name.text = item.seller.company
            text_view_order_quantity.text = item.quantity.toString()

            val decimalFormat = DecimalFormat("0.###").format(item.price)
            text_view_order_price.text = "$$decimalFormat"
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: MarketResponse)
    }
}
