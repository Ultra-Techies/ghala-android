package com.ultratechies.ghala.ui.warehouses

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.Warehouse
import com.ultratechies.ghala.databinding.FragmentWhBottomsheetBinding
import com.ultratechies.ghala.utils.hideKeyboard
import com.ultratechies.ghala.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class NewWarehouseBottomSheetFragment(var addNewWarehouseCallback : ()->Unit ) : BottomSheetDialogFragment(), View.OnClickListener {


    private lateinit var binding: FragmentWhBottomsheetBinding
    private val viewModel: BottomSheetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWhBottomsheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = STATE_EXPANDED
        }
        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        globalVariables()

        setOnClickListeners()

        addWarehouseListener()

        errorListener()

    }


    private fun setOnClickListeners() {
        binding.buttonAddWarehouse.setOnClickListener(this)
        binding.tvEndWarehouse.setOnClickListener(this)
    }

    private fun globalVariables() {
        binding.pbBottomSheet.visibility = GONE
        binding.etWarehouseName.showKeyboard()
    }

    override fun onClick(view: View) {
        when (view) {
            binding.buttonAddWarehouse -> addNewWarehouse()
            binding.tvEndWarehouse -> closeAddWarehouseBottomSheet()
        }
    }

    private fun addNewWarehouse() {
        if (binding.etWarehouseName.text.isNullOrEmpty()) {
            binding.etWarehouseLocation.error = getString(R.string.error_warehouse_name)
            return
        }

        if (binding.etWarehouseName.text.isNullOrEmpty()) {
            binding.etWarehouseLocation.error = getString(R.string.error_warehouse_location)
            return
        }


        val location = binding.etWarehouseLocation.text.trim().toString()
        val name = binding.etWarehouseName.text.trim().toString()

        val warehouse = Warehouse(
            id,
            name = name,
            location = location,
        )
        addWarehouse(warehouse)
    }


    private fun closeAddWarehouseBottomSheet() {
        dismiss()
    }

    private fun addWarehouseListener(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newWarehouseResponse.collectLatest {
                if (!it.message.isNullOrBlank()) {
                    binding.pbBottomSheet.visibility = GONE
                    Snackbar.make(
                        dialog?.window!!.decorView,
                        "Warehouse added successfully",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    addNewWarehouseCallback.invoke()
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(1000)
                        dismiss()
                    }
                }
            }
        }
    }

    private fun errorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (lifecycle.currentState >= Lifecycle.State.STARTED) {
                viewModel.errorResponse.collectLatest {
                    binding.pbBottomSheet.visibility = GONE
                    Snackbar.make(
                        dialog?.window!!.decorView,
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun addWarehouse( warehouse: Warehouse){
        binding.root.hideKeyboard()
        binding.pbBottomSheet.visibility = VISIBLE
        Snackbar.make(dialog?.window!!.decorView, "Adding warehouse...", Snackbar.LENGTH_LONG)
            .show()
        viewModel.newWarehouse(warehouse)
    }


    override fun onDestroy() {
        binding.root.hideKeyboard()
        super.onDestroy()
    }

}