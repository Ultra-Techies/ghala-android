package com.ultratechies.ghala.ui.warehouses

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.Warehouse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.databinding.WarehousesFragmentBinding
import com.ultratechies.ghala.utils.handleApiError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WarehousesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

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

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.warehouses.observe(viewLifecycleOwner) { it ->
            when (it) {
                is APIResource.Success -> {
                    it.value.forEach {
                        binding.swipeContainer.isRefreshing = false
                        warehouses.add(it)
                    }

                    showEmptyState(View.GONE)
                    var warehouseAdapter = WarehouseAdapter(warehouses)
                    Log.d("WarehousesFragment", "WarehouseAdapter: $warehouseAdapter")
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
                    var warehouseAdapter = WarehouseAdapter(warehouses)
                    binding.warehousesRecycler.itemAnimator = DefaultItemAnimator()
                    binding.warehousesRecycler.adapter = warehouseAdapter
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

}