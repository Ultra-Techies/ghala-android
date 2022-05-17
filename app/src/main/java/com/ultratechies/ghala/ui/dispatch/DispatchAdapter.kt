package com.ultratechies.ghala.ui.dispatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.responses.deliverynotes.FetchDeliveryNotesResponseItem
import com.ultratechies.ghala.databinding.ListItemDispatchBinding


class DispatchAdapter : RecyclerView.Adapter<DispatchAdapter.DispatchViewHolder>() {
    private var dispatchNoteCallback: ((FetchDeliveryNotesResponseItem) -> Unit)? = null

    class DispatchViewHolder(val binding: ListItemDispatchBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun onItemClick(onItemClick: (FetchDeliveryNotesResponseItem) -> Unit) {
        this.dispatchNoteCallback = onItemClick
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DispatchViewHolder {
        val binding =
            ListItemDispatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DispatchViewHolder(binding)
    }

    private val diffUtil = object : DiffUtil.ItemCallback<FetchDeliveryNotesResponseItem>() {
        override fun areItemsTheSame(
            oldItem: FetchDeliveryNotesResponseItem,
            newItem: FetchDeliveryNotesResponseItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FetchDeliveryNotesResponseItem,
            newItem: FetchDeliveryNotesResponseItem
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(dispatchResponse: List<FetchDeliveryNotesResponseItem>) {
        asyncListDiffer.submitList(dispatchResponse)
    }


    override fun onBindViewHolder(holder: DispatchViewHolder, position: Int) {
        val dispatchData = asyncListDiffer.currentList[position]
        val context = holder.binding.root.context
        holder.binding.apply {
            tvDispatchCode.text =
                StringBuilder(context.getString(R.string.dispatch_code, dispatchData.noteCode))
            textViewOrderQuantity.text = StringBuilder(
                context.getString(
                    R.string.order,
                    dispatchData.orders.size.toString()
                )
            )
            textViewRoute.text =
                StringBuilder(context.getString(R.string.route, dispatchData.route))
            textViewOrderDeliveryWindow.text = StringBuilder(
                context.getString(
                    R.string.delivery_window,
                    dispatchData.orders[0].deliveryWindow
                ).lowercase().replaceFirstChar { it.uppercase() }
            )
            textViewDispatchStatus.text =
                dispatchData.status.lowercase().replaceFirstChar { it.uppercase() }

            when (textViewDispatchStatus.text) {
                "Pending" -> {
                    holder.binding.textViewDispatchStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.red
                        )
                    )
                }
                "Dispatched" -> {
                    holder.binding.textViewDispatchStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.grey
                        )
                    )
                }
                "Completed" -> {
                    holder.binding.textViewDispatchStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.teal
                        )
                    )
                }
            }

            if (textViewDispatchStatus.text == "Pending") {
                textViewActions.text = StringBuilder(context.getString(R.string.dispatch_status))
                llActionButton.visibility = View.VISIBLE
            } else {
                llActionButton.visibility = View.GONE
            }

            holder.binding.llActionButton.setOnClickListener {
                dispatchNoteCallback?.invoke(dispatchData)
            }
        }

    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

}