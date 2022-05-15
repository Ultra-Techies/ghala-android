package com.ultratechies.ghala.ui.dispatch

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.data.models.responses.deliverynotes.FetchDeliveryNotesResponseItem
import com.ultratechies.ghala.databinding.DispatchFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DispatchFragment : Fragment() {
    private lateinit var binding: DispatchFragmentBinding
    private lateinit var dispatchAdapter: DispatchAdapter
    private val viewModel: DispatchViewModel by viewModels()

    private var data = mutableListOf<FetchDeliveryNotesResponseItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DispatchFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.requireActivity()

        getDispatch()
        onRefresh()
        setUpAdapter()
        fetchDeliveryNotesListener()
        fetchDeliveryNotesErrorListener()
        changeDeliveryNoteStatusListener()


    }

    private fun getDispatch() {
        viewModel.getFetchDeliveryNotes()
    }

    private fun onRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            getDispatch()
        }
    }

    private fun setUpAdapter() {
        dispatchAdapter = DispatchAdapter()
        dispatchAdapter.onItemClick { fetchNoteResponse ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Dispatch this order")
                .setMessage("Dispatch the order" + " " + fetchNoteResponse.noteCode)
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()

                    binding.pbDispatch.visibility = View.VISIBLE
                    viewModel.changeDeliveryNoteStatus(id=fetchNoteResponse.id, 1)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        binding.recyclerViewDispatch.apply {
            adapter = dispatchAdapter
            layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
    }


    private fun changeDeliveryNoteStatusListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.changeDeliveryNoteStatus.collect {
                    binding.pbDispatch.visibility = View.GONE
                }
            }
        }
    }


    private fun fetchDeliveryNotesErrorListener() {
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

    private fun fetchDeliveryNotesListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchDeliveryNotes.collect { list ->
                    binding.swipeContainer.isRefreshing = false
                    data.clear()
                    data.addAll(list)
                    displayData(list)
                }
            }
        }
    }

    private fun displayData(list: List<FetchDeliveryNotesResponseItem>) {
        if (list.isEmpty()) {
            binding.tvEmptyDispatchItems.visibility = View.VISIBLE
            binding.recyclerViewDispatch.visibility = View.GONE
        } else {
            binding.recyclerViewDispatch.visibility = View.VISIBLE
            binding.tvEmptyDispatchItems.visibility = View.GONE
            dispatchAdapter.saveData(list)
            binding.recyclerViewDispatch.scrollToPosition(
                0
            )
            Log.d("-----", list.toString())
        }
    }

}