package com.ultratechies.ghala.ui.users

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ultratechies.ghala.data.models.responses.user.FetchAllUsersResponse
import com.ultratechies.ghala.databinding.ListItemUsersBinding

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UsersAdapterViewHolder>() {
    class UsersAdapterViewHolder(var binding: ListItemUsersBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapterViewHolder {
        val binding =
            ListItemUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersAdapterViewHolder(binding)
    }

    private val diffUtil = object : DiffUtil.ItemCallback<FetchAllUsersResponse>() {
        override fun areItemsTheSame(
            oldItem: FetchAllUsersResponse,
            newItem: FetchAllUsersResponse
        ): Boolean {
            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(
            oldItem: FetchAllUsersResponse,
            newItem: FetchAllUsersResponse
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    fun saveData(usersResponse: List<FetchAllUsersResponse>) {
        asyncListDiffer.submitList(usersResponse)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UsersAdapterViewHolder, position: Int) {
        val usersData = asyncListDiffer.currentList[position]
        holder.binding.apply {
            textviewUserName.text = "${usersData.firstName} ${usersData.lastName}"
            textViewUserRole.text = usersData.role
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }
}