package com.ultratechies.ghala.ui.inventory

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.data.models.requests.inventory.AddInventoryRequest
import com.ultratechies.ghala.databinding.FragmentInventoryBottomsheetBinding
import com.ultratechies.ghala.utils.hideKeyboard
import com.ultratechies.ghala.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddInventoryBottomFragment(var addNewInventoryCallback: () -> Unit) :
    BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentInventoryBottomsheetBinding
    private val viewModel: AddInventoryBottomSheetViewModel by viewModels()

    companion object {
        const val TAG = "AddInventoryBottomSheetFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInventoryBottomsheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextProductName.showKeyboard()

        setUpOnClickListeners()
        addInventoryItemListeners()
        addInventoryItemErrorListeners()

    }


    private fun setUpUi() {
        binding.pbBottomSheet.visibility = View.GONE
        binding.addInventoryButton.isEnabled = true
    }

    private fun setUpOnClickListeners() {
        binding.addInventoryButton.setOnClickListener(this)
        binding.close.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.addInventoryButton -> addInventory()
            binding.close -> closeAddTaskBottomSheet()
        }
    }

    private fun addInventory() {
        binding.apply {
            pbBottomSheet.visibility = View.VISIBLE
            addInventoryButton.isEnabled = false

            val productName = editTextProductName.text.trim()
            val productPrice = editTextProductPrice.text.trim()
            val productQuantity = editTextProductQuantity.text.trim()
            val itemCategory = categorySpinner.selectedItem

            if (editTextProductName.text.isNullOrEmpty()) {
                editTextProductName.error = "Please Enter Product Name"
                setUpUi()
                return
            }
            if (categorySpinner.selectedItemPosition == 0) {
                Toast.makeText(
                    requireContext(),
                    "Please Select a warehouse",
                    Toast.LENGTH_SHORT
                )
                    .show()
                setUpUi()
                return
            }
            if (productPrice.isNullOrEmpty()) {
                editTextProductPrice.error = "Please Enter Product Price"
                setUpUi()
                return
            }
            if (productQuantity.isNullOrEmpty()) {
                editTextProductQuantity.error = "Please Enter Product Quantity"
                setUpUi()
                return
            }

            val addInventoryRequest = AddInventoryRequest(
                category = itemCategory as String,
                name = productName.toString(),
                ppu = productPrice.toString(),
                quantity = productQuantity.toString(),
                status = "AVAILABLE",
                warehouseId = "4"
            )

            addInventoryItem(addInventoryRequest)

        }
    }

    private fun addInventoryItem(addInventoryRequest: AddInventoryRequest) {
        binding.root.hideKeyboard()
        binding.pbBottomSheet.visibility = View.VISIBLE
        Snackbar.make(dialog?.window!!.decorView, "Adding your task...", Snackbar.LENGTH_LONG)
            .show()
        viewModel.addInventory(addInventoryRequest)
    }

    private fun addInventoryItemListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addInventoryItem.collect {
                    binding.pbBottomSheet.visibility = View.GONE
                    Snackbar.make(
                        dialog?.window!!.decorView,
                        "Task added successfully",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    addNewInventoryCallback.invoke()
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(1000)
                        dismiss()
                    }
                }
            }
        }
    }

    private fun addInventoryItemErrorListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {}
            viewModel.errorMessage.collect {
                Snackbar.make(
                    dialog?.window!!.decorView,
                    it,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(1000)
                    dismiss()
                }
            }
        }
    }


    private fun closeAddTaskBottomSheet() {
        dismiss()
    }


}