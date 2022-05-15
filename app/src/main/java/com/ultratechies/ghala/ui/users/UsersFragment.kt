package com.ultratechies.ghala.ui.users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.data.models.responses.user.FetchAllUsersResponse
import com.ultratechies.ghala.databinding.FragmentUsersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsersFragment : Fragment() {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var usersAdapter: UsersAdapter
    private val viewModel: UsersViewModel by viewModels()

    private var data = mutableListOf<FetchAllUsersResponse>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUsers()
        onRefresh()
        setUpAdapter()
        fetchUsersListener()
        fetchUsersErrorListener()
    }

    private fun fetchUsersErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect {
                    binding.swipeContainer.isRefreshing = false
                    Snackbar.make(
                        binding.root,
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchUsersListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchAllUsersResponse.collect { list ->
                    binding.swipeContainer.isRefreshing = false
                    data.clear()
                    data.addAll(list)
                    displayData(list)
                }
            }
        }
    }

    private fun displayData(list: List<FetchAllUsersResponse>) {
        if (list.isEmpty()) {
            binding.tvEmptyUsers.visibility = View.VISIBLE
            binding.rvUsers.visibility = View.GONE
        } else {
            binding.tvEmptyUsers.visibility = View.GONE
            binding.rvUsers.visibility = View.VISIBLE
            usersAdapter.saveData(list)
            binding.rvUsers.scrollToPosition(
                0
            )
            Log.d("-----", list.toString())
        }
    }

    private fun setUpAdapter() {
        usersAdapter = UsersAdapter()
        binding.rvUsers.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }

    private fun onRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            getUsers()
        }
    }

    private fun getUsers() {
        viewModel.fetchAllUsers()
    }

}