package com.ultratechies.ghala.ui.orders

import android.os.Bundle
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.data.models.requests.deliverynotes.CreateDeliveryNoteRequest
import com.ultratechies.ghala.data.models.responses.orders.OrderResponseItem
import com.ultratechies.ghala.databinding.OrdersFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : Fragment() {
    private lateinit var binding: OrdersFragmentBinding
    private val viewModel: OrdersViewModel by viewModels()
    private val deliveryNoteViewModel: CreateDeliveryNotesViewModel by viewModels()
    private lateinit var ordersAdapter: OrdersAdapter

    private var data = mutableListOf<OrderResponseItem>()

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

        getOrders()
        filterOrdersByStatus()
        setUpOrderAdapter()
        onRefresh()

        fetchOrdersListeners()
        fetchOrdersErrorListener()

        createDeliveryNoteListeners()
        createDeliveryNoteErrorListeners()


    }

    private fun getOrders() {
        binding.swipeContainer.isRefreshing = true
        viewModel.fetchOrders()
    }


    private fun filterOrdersByStatus() {
        binding.spinnerOrderStatus.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 == 0) {
                        displayData(data)
                    } else {
                        val filter = data.filter {
                            it.status.lowercase() == binding.spinnerOrderStatus.selectedItem.toString()
                                .lowercase()
                        }
                        displayData(filter)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
    }

    private fun setUpOrderAdapter() {
        ordersAdapter = OrdersAdapter()
        ordersAdapter.onItemClick { orderResponseItem ->
        }

        val recyclerView = binding.recyclerViewOrders
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = ordersAdapter

        generateDeliveryNote()

    }

    private fun generateDeliveryNote() {
        binding.DeliveryNoteButton.setOnClickListener {
            val list = ordersAdapter.returnDeliveryNotesModels()
            if (list.isEmpty()) {
                Toast.makeText(
                    context,
                    "Select Orders to create delivery note",
                    Toast.LENGTH_SHORT,
                ).show()
            } else
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Create Delivery Note ")
                    .setMessage("Create Delivery Note Containing Selected Orders")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        val addDeliveryNote = CreateDeliveryNoteRequest(
                            deliverWindow = list[0].deliveryWindow,
                            orderIds = list.map { it.id },
                            route = list[0].route,
                            warehouseId = list[0].warehouseId

                        )
                        createDeliveryNote(addDeliveryNote)
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    private fun onRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            viewModel.fetchOrders()
        }
    }

    private fun fetchOrdersListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getOrders.collect {
                    binding.swipeContainer.isRefreshing = false
                    data.clear()
                    data.addAll(it)
                    displayData(it)
                }
            }
        }
    }

    private fun displayData(list: List<OrderResponseItem>) {
        if (list.isEmpty()) {
            binding.tvEmptyOrderItems.visibility = View.VISIBLE
            binding.recyclerViewOrders.visibility = View.GONE
        } else {
            ordersAdapter.saveData(list)
            binding.tvEmptyOrderItems.visibility = View.GONE
            binding.recyclerViewOrders.visibility = View.VISIBLE
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

    private fun createDeliveryNote(createDeliveryNotes: CreateDeliveryNoteRequest) {
        binding.pbOrders.visibility = View.VISIBLE
        deliveryNoteViewModel.createDeliveryNote(createDeliveryNotes)
    }

    private fun createDeliveryNoteListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                deliveryNoteViewModel.createDeliveryNotes.collect {
                    ordersAdapter.clearSelectedItems()
                    viewModel.fetchOrders()
                    binding.pbOrders.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        "Delivery Note Created Successfully",
                        Snackbar.LENGTH_SHORT
                    ).show()

                }
            }
        }
    }

    private fun createDeliveryNoteErrorListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {}
            deliveryNoteViewModel.errorResponse.collect {
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