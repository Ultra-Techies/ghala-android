package com.ultratechies.ghala.ui.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ultratechies.ghala.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)

        binding.card1.setOnClickListener {
            Toast.makeText(context, "Card 1", Toast.LENGTH_SHORT).show()
        }

        binding.card2.setOnClickListener {
            Toast.makeText(context, "Card 2", Toast.LENGTH_SHORT).show()
        }

        binding.card3.setOnClickListener {
            Toast.makeText(context, "Card 3", Toast.LENGTH_SHORT).show()
        }

        binding.card4.setOnClickListener {
            Toast.makeText(context, "Card 4", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

    }

}