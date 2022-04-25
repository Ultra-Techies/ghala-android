package com.ultratechies.ghala.ui.inventory

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.InventoryResponseItem
import com.ultratechies.ghala.databinding.ListItemInventoryBinding
import com.ultratechies.ghala.utils.getFormattedNumbers



class InventoryAdapter : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {
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
            return oldItem == newItem
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
            textViewOrderQuantity.text = StringBuilder(context.getString(R.string.items,inventoryData.quantity.toString()))
            textViewCategory.text =
                StringBuilder(context.getString(R.string.txt_category, inventoryData.category))
            textViewItemId.text =
                StringBuilder(context.getString(R.string.sku, inventoryData.warehouseId.toString()))
            textViewItemAmount.text =StringBuilder(context.getString(R.string.price,getFormattedNumbers(totalPrice).toString()))
            textViewItemStatus.text = inventoryData.status.lowercase().replaceFirstChar { it.uppercase() }

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


    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }
}