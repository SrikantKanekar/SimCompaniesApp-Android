package com.example.sim.ui.resource

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.sim.ui.UICommunicationListener

abstract class BaseResourceFragment
constructor(
    @LayoutRes
    private val layoutRes: Int,
) : Fragment(layoutRes) {

    val TAG: String = "AppDebug"

    val viewModel: ResourceViewModel by activityViewModels()
    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChannel()
    }

    private fun setupChannel() = viewModel.setupChannel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }
    }
}