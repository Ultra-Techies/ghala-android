package com.ultratechies.ghala.ui.orders

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.OrderResponseItem
import com.ultratechies.ghala.databinding.ListItemOrdersBinding


class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.OrdersAdapterViewHolder>() {
    class OrdersAdapterViewHolder(var binding: ListItemOrdersBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<OrderResponseItem>() {
        override fun areItemsTheSame(
            oldItem: OrderResponseItem,
            newItem: OrderResponseItem
        ): Boolean {
            return oldItem == newItem
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersAdapterViewHolder {
        val binding =
            ListItemOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdersAdapterViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: OrdersAdapterViewHolder, position: Int) {
        val ordersData = asyncListDiffer.currentList[position]
        val context = holder.binding.root.context
        holder.binding.apply {
            tvShopName.text = ordersData.customerId
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
            "AVAILABLE" -> {
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
            "CANCELLED" -> {
                holder.binding.textViewOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }
}