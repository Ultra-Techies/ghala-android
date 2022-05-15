package com.ultratechies.ghala.ui.orders

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.data.models.requests.deliverynotes.CreateDeliveryNoteRequest
import com.ultratechies.ghala.data.models.responses.orders.OrderResponseItem
import com.ultratechies.ghala.databinding.OrdersFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrdersFragment : Fragment() {
    private lateinit var binding: OrdersFragmentBinding
    private val viewModel: OrdersViewModel by viewModels()
    private val deliveryNoteViewModel: CreateDeliveryNotesViewModel by viewModels()
    private lateinit var ordersAdapter: OrdersAdapter

    private var data = mutableListOf<OrderResponseItem>()

    @Inject
    lateinit var appDatasource: AppDatasource

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
        ordersAdapter.onItemClick {

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

                        viewLifecycleOwner.lifecycleScope.launch {
                            appDatasource.getUserFromPreferencesStore().collectLatest { user ->
                                val addDeliveryNote = CreateDeliveryNoteRequest(
                                    deliverWindow = list[0].deliveryWindow,
                                    orderIds = list.map { it.id },
                                    route = list[0].route,
                                    warehouseId = user.assignedWarehouse
                                )
                                createDeliveryNote(addDeliveryNote)
                                Log.d("---->", addDeliveryNote.toString())
                            }
                        }

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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
            binding.recyclerViewOrders.scrollToPosition(
                0
            )
        }
    }

    private fun fetchOrdersErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {}
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