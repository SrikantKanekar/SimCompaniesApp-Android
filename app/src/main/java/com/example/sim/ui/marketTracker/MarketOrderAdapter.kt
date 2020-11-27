package com.example.sim.ui.marketTracker

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
import kotlinx.android.synthetic.main.item_order.view.*
import java.text.DecimalFormat

class MarketOrderAdapter(private val interaction: Interaction? = null) :
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

        return OrdersViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_order,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrdersViewHolder -> {
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

    fun getList(): List<MarketResponse> {
        return differ.currentList
    }

    class OrdersViewHolder
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
                .into(image_view)
            itemView.text_view_company_name.text = item.seller.company
            itemView.text_view_quantity.text = item.quantity.toString()

            val decimalFormat = DecimalFormat("0.###").format(item.price)
            itemView.text_view_price.text = "$$decimalFormat"

            if (item.quality == 0) {
                itemView.rating_bar.visibility = View.INVISIBLE
            } else {
                itemView.rating_bar.numStars = item.quality
                itemView.rating_bar.rating = item.quality.toFloat()
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: MarketResponse)
    }
}
