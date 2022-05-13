package com.ultratechies.ghala.ui.warehouses

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.warehouses.Warehouse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.databinding.FragmentEwhBottomsheetBinding
import com.ultratechies.ghala.utils.handleApiError
import com.ultratechies.ghala.utils.hideKeyboard
import com.ultratechies.ghala.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditWarehouseBottomSheetFragment(var refreshListCallback: () -> Unit) :
    BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentEwhBottomsheetBinding
    private val viewModel: BottomSheetViewModel by viewModels()
    private lateinit var warehouseModel: Warehouse

    companion object {
        fun newInstance(
            item: Warehouse,
            refreshListCallback: () -> Unit
        ): EditWarehouseBottomSheetFragment {
            val bundle = Bundle()
            bundle.apply {
                putParcelable("data", item)
            }
            return EditWarehouseBottomSheetFragment(refreshListCallback).apply {
                arguments = bundle
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        warehouseModel = arguments?.getParcelable("data")!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEwhBottomsheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setText()
        setUpGlobalVariables()
        setOnClickListeners()
        editWarehouseListener()
        errorListener()
    }

    private fun setOnClickListeners() {
        binding.buttonEditWarehouse.setOnClickListener(this)
        binding.tvEndWarehouse.setOnClickListener(this)
    }

    private fun editWarehouseListener() {
        viewModel.editWarehouseResponse.observe(viewLifecycleOwner) {
            when (it) {
                is APIResource.Success -> {
                    binding.pbBottomSheet.visibility = View.GONE
                    refreshListCallback.invoke()
                    Snackbar.make(
                        dialog?.window!!.decorView,
                        "Warehouse updated successfully",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()

                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(1000)
                        dismiss()
                    }
                }
                is APIResource.Error -> {
                    binding.root.handleApiError(it)
                }
            }
        }
    }

    private fun editWarehouse(editWarehouseRequest: Warehouse) {
        binding.root.hideKeyboard()
        binding.pbBottomSheet.visibility = View.VISIBLE
        Snackbar.make(dialog?.window!!.decorView, "Saving warehouse...", Snackbar.LENGTH_LONG)
            .show()

        binding.pbBottomSheet.visibility = View.VISIBLE
        viewModel.editWarehouse(editWarehouseRequest)
    }

    private fun setText() {
        warehouseModel.apply {
            name.let {
                binding.etWarehouseName.setText(it)
            }

            location.let {
                binding.etWarehouseLocation.setText(it)
            }
        }
    }

    private fun setUpGlobalVariables() {
        binding.pbBottomSheet.visibility = View.GONE
        binding.etWarehouseName.showKeyboard()
    }

    private fun errorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorResponse.collectLatest { message ->
                    binding.pbBottomSheet.visibility = View.GONE
                    Snackbar.make(
                        dialog?.window!!.decorView,
                        message,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.buttonEditWarehouse -> submitEditedWarehouse()
            binding.tvEndWarehouse -> closeBottomSheet()
        }
    }

    private fun submitEditedWarehouse() {
        // validate
        if (binding.etWarehouseName.text.isNullOrEmpty()) {
            binding.etWarehouseName.error = getString(R.string.error_warehouse_name)
            return
        }

        if (binding.etWarehouseLocation.text.isNullOrEmpty()) {
            binding.etWarehouseLocation.error = getString(R.string.error_warehouse_location)
            return
        }

        val editWarehouseRequest = Warehouse(
            name = binding.etWarehouseName.text.trim().toString(),
            location = binding.etWarehouseLocation.text.trim().toString(),
            id = warehouseModel.id
        )

        Log.d("EDIT", editWarehouseRequest.toString())
        editWarehouse(editWarehouseRequest)
    }

    private fun closeBottomSheet() {
        dismiss()
    }

    override fun onDestroy() {
        binding.root.hideKeyboard()
        super.onDestroy()
    }
}