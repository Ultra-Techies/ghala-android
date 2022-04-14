package com.ultratechies.ghala.ui.dispatch

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ultratechies.ghala.R

class DispatchFragment : Fragment() {

    companion object {
        fun newInstance() = DispatchFragment()
    }

    private lateinit var viewModel: DispatchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dispatch_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DispatchViewModel::class.java)
        // TODO: Use the ViewModel
    }

}