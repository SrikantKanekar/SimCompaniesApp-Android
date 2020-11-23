package com.example.sim.ui.resource

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sim.R
import com.example.sim.models.Resource
import com.example.sim.ui.resource.state.ResourceStateEvent.*
import com.example.sim.util.StateMessageCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_view_resource.*

@AndroidEntryPoint
class ViewResourceFragment : BaseResourceFragment(R.layout.fragment_view_resource) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()
        getResourceById()
    }

    private fun getResourceById() {
        viewModel.setStateEvent(GetResourceByIdEvent)
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { viewState ->
                viewState.viewResourceFields.resourceDetail?.let { resourceDetail ->
                    setResourceDetailData(resourceDetail)
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

    private fun setResourceDetailData(resourceDetail: Resource) {
        Glide.with(this)
            .load(resourceDetail.image)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_launcher_background)
            .into(image_view)
        text_view_name.text = resourceDetail.name
        text_view_needed_for.text = resourceDetail.tracking.toString()
        Log.d(TAG, "setResourceDetailData: $resourceDetail")
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearResourceDetailData()
    }
}