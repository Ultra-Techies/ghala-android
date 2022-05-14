package com.ultratechies.ghala.ui.dispatch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ultratechies.ghala.data.models.responses.deliverynotes.FetchDeliveryNotesResponseItem
import com.ultratechies.ghala.databinding.ListItemDispatchBinding



class DispatchAdapter  : RecyclerView.Adapter<DispatchAdapter.DispatchViewHolder>(){

    class DispatchViewHolder(val binding: ListItemDispatchBinding):  RecyclerView.ViewHolder(binding.root)

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
            oldItem:FetchDeliveryNotesResponseItem ,
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
        holder.binding.apply {
            tvDispatchCode.text = dispatchData.noteCode
            textViewOrderQuantity.text = dispatchData.orders.size.toString()
            textViewRoute.text = dispatchData.route
            textViewOrderDeliveryWindow.text = dispatchData.deliveryWindow
            textViewDispatchStatus.text = dispatchData.status
        }

    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

}