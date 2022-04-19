package com.ultratechies.ghala.ui.warehouses

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var viewModel: WarehousesViewModel
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

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WarehousesViewModel::class.java)

        viewModel.warehouses.observe(viewLifecycleOwner) {
            when (it) {
                is APIResource.Success -> {
                    it.value.warehouses.forEach { it ->
                        warehouses.add(it)
                    }
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    binding.root.handleApiError(it)
                    binding.swipeContainer.isRefreshing = false
                    showEmptyState(View.VISIBLE)
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