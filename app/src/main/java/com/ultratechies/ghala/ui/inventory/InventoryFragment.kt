package com.ultratechies.ghala.ui.inventory

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
import com.ultratechies.ghala.databinding.InventoryFragmentBinding
import com.ultratechies.ghala.ui.orders.OrdersFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InventoryFragment : Fragment() {
    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var binding: InventoryFragmentBinding

    companion object {
        fun newInstance() = OrdersFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InventoryFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInventoryAdapter()
        binding.swipeContainer.isRefreshing = false
        viewModel.fetchInventory()
        fetchInventoryListener()
        fetchInventoryErrorListener()
        onRefresh()
    }

    private fun onRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            viewModel.fetchInventory()
        }
    }

    private fun fetchInventoryErrorListener() {
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

    private fun fetchInventoryListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchInventory.collect {
                    binding.swipeContainer.isRefreshing = false
                    if (it.isEmpty()) {
                        binding.tvEmptyInventoryItems.visibility = View.VISIBLE
                    } else {
                        inventoryAdapter.saveData(it)
                    }
                }
            }
        }
    }

    private fun setupInventoryAdapter() {
        inventoryAdapter = InventoryAdapter()
        val recyclerView = binding.recyclerViewInventory
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = inventoryAdapter
    }


}