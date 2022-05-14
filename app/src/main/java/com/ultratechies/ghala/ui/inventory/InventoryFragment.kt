package com.ultratechies.ghala.ui.inventory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.data.models.responses.inventory.InventoryResponseItem
import com.ultratechies.ghala.databinding.InventoryFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InventoryFragment : Fragment() {
    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var binding: InventoryFragmentBinding

    private var data = mutableListOf<InventoryResponseItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = InventoryFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.requireActivity()

        setupInventoryAdapter()
        getInventory()

        binding.swipeContainer.isRefreshing = false
        fetchInventoryListener()
        fetchInventoryErrorListener()
        deleteInventoryListener()
        deleteInventoryErrorListener()
        onRefresh()

        binding.addNewInventory.setOnClickListener {
            val addInventoryBottomSheet = AddInventoryBottomFragment {
                getInventory()
            }
            addInventoryBottomSheet.show(childFragmentManager, AddInventoryBottomFragment.TAG)
        }
        binding.spinnerCategoryStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        displayData(data)
                    } else {
                        val filter =
                            data.filter {
                                it.status.lowercase() == binding.spinnerCategoryStatus.selectedItem.toString()
                                    .lowercase()
                            }
                        displayData(filter)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
    }


    private fun getInventory() {
        viewModel.fetchInventory()
    }

    private fun onRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            getInventory()
        }
    }

    private fun fetchInventoryErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchInventory.collect {
                    binding.swipeContainer.isRefreshing = false
                    data.clear()
                    data.addAll(it)
                    displayData(it)
                }
            }
        }
    }

    private fun displayData(list: List<InventoryResponseItem>) {
        if (list.isEmpty()) {
            binding.tvEmptyInventoryItems.visibility = View.VISIBLE
            binding.recyclerViewInventory.visibility = View.GONE
        } else {
            binding.recyclerViewInventory.visibility = View.VISIBLE
            binding.tvEmptyInventoryItems.visibility = View.GONE
            inventoryAdapter.saveData(list)
            binding.recyclerViewInventory.scrollToPosition(
                0
            )
        }
    }

    private fun setupInventoryAdapter() {
        inventoryAdapter = InventoryAdapter()
        inventoryAdapter.onItemClick { inventoryResponseItem ->
            val editBtmFrag = EditInventoryBottomFragment.newInstance(inventoryResponseItem)
            editBtmFrag.onRefresh {
                // make network
                getInventory()
            }
            editBtmFrag.show(childFragmentManager, "edit_frag")
        }
        inventoryAdapter.onItemDelete {
            deleteInventoryItem(sku = it.sku)
            Log.d("SKU", it.sku.toString())
        }

        binding.recyclerViewInventory.apply {
            adapter = inventoryAdapter
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }

    }

    private fun deleteInventoryItem(sku: Int) {
        viewModel.deleteInventory(sku)
    }

    private fun deleteInventoryListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.deleteInventory.collect {
                        getInventory()
                        binding.swipeContainer.isRefreshing = false
                        Toast.makeText(
                            requireContext(),
                            "Task Deleted Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun deleteInventoryErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect {
                    binding.swipeContainer.isRefreshing = false
                    Toast.makeText(
                        requireContext(),
                        it,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getInventory()
    }
}