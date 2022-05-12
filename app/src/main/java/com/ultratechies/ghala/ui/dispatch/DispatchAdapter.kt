package com.ultratechies.ghala.ui.dispatch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ultratechies.ghala.data.models.responses.deliverynotes.FetchDeliveryNotesResponse
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

    private val diffUtil = object : DiffUtil.ItemCallback<FetchDeliveryNotesResponse>() {
        override fun areItemsTheSame(
            oldItem: FetchDeliveryNotesResponse,
            newItem: FetchDeliveryNotesResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem:FetchDeliveryNotesResponse ,
            newItem: FetchDeliveryNotesResponse
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(dispatchResponse: List<FetchDeliveryNotesResponse>) {
        asyncListDiffer.submitList(dispatchResponse)
    }


    override fun onBindViewHolder(holder: DispatchViewHolder, position: Int) {
        val dispatchData = asyncListDiffer.currentList[position]
        val context = holder.binding.root.context
        holder.binding.apply {
            tvDispatchCode.text = dispatchData.noteCode
            textViewOrderQuantity.text = dispatchData.orders.size.toString()
            textViewRoute.text = dispatchData.route.toString()
            textViewOrderDeliveryWindow.text = dispatchData.deliveryWindow.toString()
            textViewDispatchStatus.text = dispatchData.status
        }

    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

}