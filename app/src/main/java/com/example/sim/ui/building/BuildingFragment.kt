package com.example.sim.ui.building

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.sim.R
import com.example.sim.models.building.Building
import com.example.sim.ui.resource.ResourceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_resources.*

@AndroidEntryPoint
class BuildingFragment : Fragment(R.layout.fragment_buildings), BuildingAdapter.Interaction {
    private val viewModel by viewModels<ResourceViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BuildingAdapter(this)

        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = adapter

        viewModel.requestBuildings()

        viewModel.getBuildings().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun onItemSelected(position: Int, item: Building) {

    }
}