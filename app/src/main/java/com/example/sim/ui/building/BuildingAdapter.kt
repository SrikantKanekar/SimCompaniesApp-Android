package com.example.sim.ui.building

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sim.R
import com.example.sim.api.building.response.BuildingResponse
import com.example.sim.models.Building
import kotlinx.android.synthetic.main.item_building.view.*

class BuildingAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Building>() {

        override fun areItemsTheSame(oldItem: Building, newItem: Building): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Building, newItem: Building): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return BuildingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_building,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BuildingViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Building>) {
        differ.submitList(list)
    }

    class BuildingViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        private val TAG = "BuildingAdapter"

        fun bind(item: Building) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            Glide.with(itemView)
                .load(item.image)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_launcher_background)
                .into(image_view)
            itemView.text_view_user_name.text = item.name
            Log.d(TAG, "bind: $item")
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Building)
    }
}
