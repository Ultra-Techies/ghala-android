package com.ultratechies.ghala.ui.dispatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.data.models.responses.deliverynotes.FetchDeliveryNotesResponse
import com.ultratechies.ghala.data.models.responses.inventory.InventoryResponseItem
import com.ultratechies.ghala.databinding.DispatchFragmentBinding
import com.ultratechies.ghala.ui.inventory.InventoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DispatchFragment : Fragment() {

    private lateinit var binding: DispatchFragmentBinding

    private lateinit var dispatchAdapter: DispatchAdapter

    private val viewModel: DispatchViewModel by viewModels()

    private var data = mutableListOf<FetchDeliveryNotesResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DispatchFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        fetchDeliveryNotesListener()
        fetchDeliveryNotesErrorListener()


    }

    private fun fetchDeliveryNotesErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect {
                    binding.swipeContainer.isRefreshing = false
                    Snackbar.make(
                        binding.root,
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchDeliveryNotesListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchDeliveryNotes.collect {
                    binding.swipeContainer.isRefreshing = false
                    data.clear()
                    data.addAll(it)
                    displayData(it)
                }
            }
        }
    }
    private fun displayData(list: List<FetchDeliveryNotesResponse>) {
        if (list.isEmpty()) {
            binding.tvEmptyDispatchItems.visibility = View.VISIBLE
            binding.recyclerViewDispatch.visibility = View.GONE
        } else {
            binding.recyclerViewDispatch.visibility = View.VISIBLE
            binding.tvEmptyDispatchItems.visibility = View.GONE
            dispatchAdapter.saveData(list)
            binding.recyclerViewDispatch.scrollToPosition(
                0
            )
        }
    }


    private fun setUpAdapter() {
        dispatchAdapter = DispatchAdapter()
        binding.recyclerViewDispatch.apply {
            adapter = DispatchAdapter()
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

}