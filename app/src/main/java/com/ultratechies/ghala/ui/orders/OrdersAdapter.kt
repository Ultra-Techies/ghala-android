package com.ultratechies.ghala.ui.orders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.orders.OrderResponseItem
import com.ultratechies.ghala.databinding.ListItemOrdersBinding


class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.OrdersAdapterViewHolder>() {
    private var deliveryNotesModels = mutableListOf<OrderResponseItem>()
    private var createDeliveryNoteCallback: ((OrderResponseItem) -> Unit)? = null

    class OrdersAdapterViewHolder(var binding: ListItemOrdersBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun onItemClick(onItemClick: (OrderResponseItem) -> Unit) {
        this.createDeliveryNoteCallback = onItemClick
    }

    fun returnDeliveryNotesModels(): List<OrderResponseItem> {
        return deliveryNotesModels
    }

    private val diffUtil = object : DiffUtil.ItemCallback<OrderResponseItem>() {
        override fun areItemsTheSame(
            oldItem: OrderResponseItem,
            newItem: OrderResponseItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OrderResponseItem,
            newItem: OrderResponseItem
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(orderResponse: List<OrderResponseItem>) {
        asyncListDiffer.submitList(orderResponse)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearSelectedItems() {
        if (deliveryNotesModels.isNotEmpty()) {
            deliveryNotesModels.clear()
            notifyDataSetChanged()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersAdapterViewHolder {
        val binding =
            ListItemOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdersAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdersAdapterViewHolder, position: Int) {
        val ordersData = asyncListDiffer.currentList[position]
        val context = holder.binding.root.context
        holder.binding.apply {
            tvShopName.text = ordersData.customerName
            /*  textViewOrderAmount.text = StringBuilder(context.getString(R.string.price,getFormattedNumbers( ordersData.items.fold( 0 ) { initialAmount, item ->
                  initialAmount + item.totalPrice
              }).toString()))*/
            textViewOrderAmount.text =
                StringBuilder(context.getString(R.string.price, ordersData.value.toString()))
            textViewOrderId.text =
                StringBuilder(context.getString(R.string.txt_orderId, ordersData.orderCode))
            textViewOrderStatus.text =
                ordersData.status.lowercase().replaceFirstChar { it.uppercase() }
            textViewOrderDeliveryDate.text =
                StringBuilder(context.getString(R.string.txt_delivery_date, ordersData.due))
            textViewOrderQuantity.text = StringBuilder(
                context.getString(
                    R.string.items,
                    ordersData.items.fold(0) { initialQuantity, item ->
                        initialQuantity + item.quantity
                    }.toString()
                )
            )

        }
        when (ordersData.status) {
            "PROCESSED" -> {
                holder.binding.textViewOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.teal
                    )
                )

            }
            "PENDING" -> {
                holder.binding.textViewOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.grey
                    )
                )
            }
            "DISPATCHED" -> {
                holder.binding.textViewOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
            }
            "DELIVERED" ->{
                holder.binding.textViewOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.teal
                    )
                )
            }
        }

        holder.binding.orderCard.setOnClickListener {
            createDeliveryNoteCallback?.invoke(ordersData)
        }

         holder.binding.checkboxOrders.setOnCheckedChangeListener(null)
         holder.binding.orderCard.setOnLongClickListener(null)

        if (deliveryNotesModels.contains(ordersData)) {
            holder.binding.checkboxOrders.visibility = View.VISIBLE
            holder.binding.checkboxOrders.isChecked = true
        } else {
            holder.binding.checkboxOrders.visibility = View.GONE
            holder.binding.checkboxOrders.isChecked = false
        }

        holder.binding.orderCard.setOnLongClickListener(View.OnLongClickListener {
            holder.binding.checkboxOrders.visibility = View.VISIBLE
            holder.binding.checkboxOrders.isChecked = true
            return@OnLongClickListener true
        })

        holder.binding.checkboxOrders.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!deliveryNotesModels.contains(ordersData))
                    deliveryNotesModels.add(ordersData)
            } else {
                holder.binding.checkboxOrders.visibility = View.GONE
                deliveryNotesModels.remove(ordersData)
            }
        }

    }


    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }
}