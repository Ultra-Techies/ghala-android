package com.ultratechies.ghala.ui.inventory

import android.app.Dialog
import android.os.Bundle
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
import com.ultratechies.ghala.data.models.requests.inventory.EditInventoryRequest
import com.ultratechies.ghala.data.models.responses.inventory.InventoryResponseItem
import com.ultratechies.ghala.databinding.FragmentInventoryEditBottomsheetBinding
import com.ultratechies.ghala.utils.hideKeyboard
import com.ultratechies.ghala.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditInventoryBottomFragment :
    BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentInventoryEditBottomsheetBinding
    private lateinit var inventoryModel: InventoryResponseItem
    private val viewModel: EditInventoryBottomSheetViewModel by viewModels()

    private var categoryName: String? = null

    private var refreshListCallback: (() -> Unit)? = null

    fun onRefresh(cb: () -> Unit) {
        this.refreshListCallback = cb
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInventoryEditBottomsheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(
            item: InventoryResponseItem
        ): EditInventoryBottomFragment {
            val bundle = Bundle()
            bundle.apply {
                putParcelable("data", item)
            }
            return EditInventoryBottomFragment().apply {
                arguments = bundle
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inventoryModel = arguments?.getParcelable("data")!!
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
        setText()

        setUpGlobalVariables()
        setOnClickListeners()

        editInventoryListeners()
        editInventoryErrorListeners()
    }


    private fun setText() {
        inventoryModel.apply {
            name.let {
                binding.editProductName.setText(it)
            }
            ppu.let {
                binding.editProductPrice.setText(it.toString())
            }
            quantity.let {
                binding.editProductQuantity.setText(it.toString())
            }
            category.let {
                binding.tvPreviousCategory.text =
                    StringBuilder(getString(R.string.txt_prev_category, it))
            }

        }
        categoryName = inventoryModel.category
    }

    private fun setUpGlobalVariables() {
        binding.pbBottomSheet.visibility = View.GONE
        binding.editProductName.showKeyboard()
    }

    private fun setOnClickListeners() {
        binding.editInventoryButton.setOnClickListener(this)
        binding.close.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.editInventoryButton -> editInventoryItem()
            binding.close -> closeBottomSheet()
        }
    }

    private fun editInventoryItem() {

        binding.apply {
            pbBottomSheet.visibility = View.VISIBLE
            editInventoryButton.isEnabled = false

            val productName = editProductName.text.trim()
            val productPrice = editProductPrice.text.trim()
            val productQuantity = editProductQuantity.text.trim()

            if (editProductName.text.isNullOrEmpty()) {
                editProductName.error = "Please Enter Product Name"
                binding.pbBottomSheet.visibility = View.GONE
                binding.editInventoryButton.isEnabled = true
                return
            }
            if (editProductQuantity.text.isNullOrEmpty()) {
                editProductQuantity.error = "Please Enter Product Quantity"
                binding.pbBottomSheet.visibility = View.GONE
                binding.editInventoryButton.isEnabled = true
                return
            }
            if (editProductPrice.text.isNullOrEmpty()) {
                editProductPrice.error = "Please Enter Product Price"
                binding.pbBottomSheet.visibility = View.GONE
                binding.editInventoryButton.isEnabled = true
                return
            }
            if (categorySpinner.selectedItemPosition != 0) {
                categoryName = categorySpinner.selectedItem.toString()
            }

            val editInventoryRequest = EditInventoryRequest(
                category = categoryName!!,
                name = productName.toString(),
                ppu = productPrice.toString(),
                quantity = productQuantity.toString(),
                sku = inventoryModel.sku,
                status = inventoryModel.status,
                warehouseId = inventoryModel.warehouseId.toString()
            )
            editInventory(editInventoryRequest)


        }
    }

    private fun editInventory(editInventoryRequest: EditInventoryRequest) {
        binding.root.hideKeyboard()
        binding.pbBottomSheet.visibility = View.VISIBLE
        Snackbar.make(dialog?.window!!.decorView, "Editing your task...", Snackbar.LENGTH_LONG)
            .show()
        viewModel.editInventoryItem(editInventoryRequest)
    }

    private fun editInventoryListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editInventory.collect {
                    binding.pbBottomSheet.visibility = View.GONE
                    refreshListCallback?.invoke()
                    Snackbar.make(
                        dialog?.window!!.decorView,
                        "Task edited successfully",
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
    }

    private fun editInventoryErrorListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collectLatest {
                    binding.pbBottomSheet.visibility = View.GONE
                    Snackbar.make(
                        dialog?.window!!.decorView,
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun closeBottomSheet() {
        dismiss()
    }
}