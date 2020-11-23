package com.example.sim.ui.building

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sim.R
import com.example.sim.models.Building
import com.example.sim.models.Resource
import com.example.sim.ui.building.state.BuildingStateEvent.*
import com.example.sim.util.StateMessageCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_view_resource.*

@AndroidEntryPoint
class ViewBuildingFragment : BaseBuildingFragment(R.layout.fragment_view_building){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()
        getBuildingByKind()
    }

    private fun getBuildingByKind() {
        viewModel.setStateEvent(GetBuildingByKindEvent)
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { viewState ->
                viewState.viewBuildingFields.buildingDetail?.let { buildingDetail ->
                    setBuildingDetailData(buildingDetail)
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
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

    private fun setBuildingDetailData(buildingDetail: Building) {
        Glide.with(this)
            .load(buildingDetail.image)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_launcher_background)
            .into(image_view)
        text_view_name.text = buildingDetail.name
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearBuildingDetailData()
    }
}