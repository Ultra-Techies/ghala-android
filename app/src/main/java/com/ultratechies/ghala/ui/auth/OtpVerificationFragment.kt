package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ultratechies.ghala.R
import com.ultratechies.ghala.databinding.FragmentOtpVerificationBinding

class OtpVerificationFragment: Fragment() {
    private lateinit var binding: FragmentOtpVerificationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.verifyButton.setOnClickListener {
            findNavController().navigate(R.id.action_otpVerificationFragment2_to_setupAccountFragment2)
        }
    }
}