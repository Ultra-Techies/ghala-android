package com.ultratechies.ghala.ui.warehouses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.Warehouse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.databinding.WarehousesFragmentBinding
import com.ultratechies.ghala.utils.handleApiError
import com.ultratechies.ghala.utils.isNetworkAvailable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WarehousesFragment() : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val warehouses = arrayListOf<Warehouse>()

    private val viewModel by viewModels<WarehousesViewModel>()
    private lateinit var binding: WarehousesFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WarehousesFragmentBinding.inflate(inflater, container, false)

        binding.swipeContainer.setOnRefreshListener(this)
        binding.swipeContainer.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )

        binding.swipeContainer.post {
            warehouses.clear()
        }

        viewModel.getWarehouses()

        binding.warehousesRecycler.layoutManager = LinearLayoutManager(context)

        binding.addNewWarehouseFAB.setOnClickListener {
            //check if network is available first
            if (!isNetworkAvailable(requireContext())) {
                Snackbar.make(
                    binding.root,
                    "No internet connection",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                val addNewWarehouseBottomSheet = NewWarehouseBottomSheetFragment{
                    onRefresh()
                }
                addNewWarehouseBottomSheet.show(childFragmentManager, NewWarehouseBottomSheetFragment.TAG)
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        warehouses.clear()

        viewModel.warehouses.observe(viewLifecycleOwner) { it ->
            when (it) {
                is APIResource.Success -> {
                    it.value.forEach {
                        binding.swipeContainer.isRefreshing = false
                        warehouses.add(it)
                    }

                    showEmptyState(View.GONE)
                    if(warehouses.isEmpty()) {
                        showEmptyState(View.VISIBLE)
                    }

                    var warehouseAdapter = WarehouseAdapter(warehouses, this)
                    binding.warehousesRecycler.itemAnimator = DefaultItemAnimator()
                    binding.warehousesRecycler.adapter = warehouseAdapter
                }
                is APIResource.Loading -> {
                    binding.swipeContainer.isRefreshing = true
                    warehouses.clear()
                }
                is APIResource.Error -> {
                    binding.root.handleApiError(it)
                    binding.swipeContainer.isRefreshing = false
                    showEmptyState(View.VISIBLE)
                    warehouses.clear()
                    var warehouseAdapter = WarehouseAdapter(warehouses, this)
                    binding.warehousesRecycler.itemAnimator = DefaultItemAnimator()
                    binding.warehousesRecycler.adapter = warehouseAdapter
                }
            }
        }

        viewModel.deletedWarehouseResponse.observe(viewLifecycleOwner) {
            when (it) {
                is APIResource.Success -> {
                    Snackbar.make(
                        binding.root,
                        "Warehouse deleted successfully",
                        Snackbar.LENGTH_LONG
                    ).show()
                    onRefresh()
                }
                is APIResource.Error -> {
                    binding.root.handleApiError(it)
                }
            }
        }
    }

    private fun showEmptyState(visible: Int) {
        binding.emptyWarehouseTV.visibility = visible
    }

    override fun onRefresh() {
        warehouses.clear()
        viewModel.getWarehouses()
    }

    fun deleteWarehouse(id: Int) {
        viewModel.deleteWarehouse(id)
    }

}