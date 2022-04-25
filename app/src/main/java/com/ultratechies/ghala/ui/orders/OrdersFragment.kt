package com.ultratechies.ghala.ui.orders

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
import com.ultratechies.ghala.databinding.OrdersFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : Fragment() {
    private lateinit var binding: OrdersFragmentBinding
    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var ordersAdapter: OrdersAdapter

    companion object {
        fun newInstance() = OrdersFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OrdersFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
/*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)
        // TODO: Use the ViewModel
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOrderAdapter()

        binding.swipeContainer.isRefreshing = true
        viewModel.fetchOrders()

        fetchOrdersListeners()
        fetchOrdersErrorListener()
        onRefresh()


    }

    private fun onRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            viewModel.fetchOrders()
        }
    }


    private fun setUpOrderAdapter() {
        ordersAdapter = OrdersAdapter()
        val recyclerView = binding.recyclerViewOrders
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = ordersAdapter

    }

    private fun fetchOrdersListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getOrders.collect {
                    binding.swipeContainer.isRefreshing = false
                    if (it.isEmpty()) {
                        binding.tvEmptyOrderItems.visibility = View.VISIBLE
                    } else {
                        ordersAdapter.saveData(it)
                    }

                }
            }
        }
    }

    private fun fetchOrdersErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorResponse.collect {
                    binding.swipeContainer.isRefreshing = false
                    Snackbar.make(
                        binding.root,
                        it,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }


}