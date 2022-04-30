package com.ultratechies.ghala.ui.inventory

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.inventory.InventoryResponseItem
import com.ultratechies.ghala.databinding.ListItemInventoryBinding
import com.ultratechies.ghala.utils.getFormattedNumbers


class InventoryAdapter : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {
    private var editInventoryCallback: ((InventoryResponseItem) -> Unit)? = null
    private var deleteInventoryCallback: ((InventoryResponseItem)->Unit)? = null

    fun onItemDelete(onDeleteClick:(InventoryResponseItem) ->Unit){
        this.deleteInventoryCallback = onDeleteClick
    }

    fun onItemClick(onItemClick: (InventoryResponseItem) -> Unit) {
        this.editInventoryCallback = onItemClick
    }

    class InventoryViewHolder(val binding: ListItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding =
            ListItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(binding)
    }

    private val diffUtil = object : DiffUtil.ItemCallback<InventoryResponseItem>() {
        override fun areItemsTheSame(
            oldItem: InventoryResponseItem,
            newItem: InventoryResponseItem
        ): Boolean {
            return oldItem.sku == newItem.sku
        }

        override fun areContentsTheSame(
            oldItem: InventoryResponseItem,
            newItem: InventoryResponseItem
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(inventoryResponse: List<InventoryResponseItem>) {
        asyncListDiffer.submitList(inventoryResponse)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val inventoryData = asyncListDiffer.currentList[position]
        val context = holder.binding.root.context
        holder.binding.apply {
            val totalPrice = inventoryData.quantity * inventoryData.ppu

            tvItemName.text = inventoryData.name.replaceFirstChar { it.uppercase() }
            textViewOrderQuantity.text =
                StringBuilder(context.getString(R.string.items, inventoryData.quantity.toString()))
            textViewCategory.text =
                StringBuilder(context.getString(R.string.txt_category, inventoryData.category))
            textViewItemId.text =
                StringBuilder(context.getString(R.string.sku, inventoryData.skuCode))
            textViewItemAmount.text = StringBuilder(
                context.getString(
                    R.string.price,
                    getFormattedNumbers(totalPrice).toString()
                )
            )
            textViewItemStatus.text =
                inventoryData.status.lowercase().replaceFirstChar { it.uppercase() }

        }
        if (inventoryData.status == "AVAILABLE") {
            holder.binding.textViewItemStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.teal
                )
            )
        } else {
            holder.binding.textViewItemStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.red
                )
            )
        }
        holder.binding.inventoryCard.setOnClickListener {
            editInventoryCallback?.invoke(inventoryData)
        }

        holder.binding.inventoryCard.setOnLongClickListener(View.OnLongClickListener {
            MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons)
                .setTitle("Delete ${inventoryData.name} ")
                .setMessage("Are you sure you want to delete ${inventoryData.name} from the Inventory?")
                .setPositiveButton("Yes") { dialog, which ->
                    deleteInventoryCallback?.invoke(inventoryData)
                }
                .setNegativeButton("No") { dialog, which ->
                    //do nothing
                }
                .show()
            return@OnLongClickListener true
        })

    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }
}