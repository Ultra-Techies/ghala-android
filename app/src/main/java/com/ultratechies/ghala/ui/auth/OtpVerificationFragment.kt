package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.databinding.FragmentOtpVerificationBinding
import com.ultratechies.ghala.ui.auth.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class OtpVerificationFragment : Fragment() {
    private lateinit var binding: FragmentOtpVerificationBinding

    /*  private val viewModel: OtpViewModel by viewModels()*/
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verifyOtpCode()
        resendOtpCode()
        fetchOTPErrorListener()
        fetchOTPlistener()
        otpResponseListener()
    }

    private fun resendOtpCode() {
        binding.tvResend.setOnClickListener {
            toggleLoading(true)
            viewModel.resendOtp()
        }
    }

    private fun fetchOTPlistener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getOTP.collect { otpResponse ->
                toggleLoading(false)
                showMessage("OTP resent successfully")
            }
        }
    }

    private fun fetchOTPErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect {
                toggleLoading(false)
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    private fun verifyOtpCode() {
        binding.verifyButton.setOnClickListener {

            binding.pbOtpVerification.visibility = View.VISIBLE
            binding.verifyButton.isEnabled = false

            if (binding.squareField.text.isNullOrEmpty()) {
                showMessage("Please Enter OTP")
                binding.pbOtpVerification.visibility = View.GONE
                binding.verifyButton.isEnabled = true
                return@setOnClickListener
            }

            viewModel.validateOtp(binding.squareField.text.toString())
        }
    }

    private fun otpResponseListener() {
        lifecycleScope.launch {
            viewModel.otpListener.collect { valid ->
                if (valid) {
                    findNavController().navigate(com.ultratechies.ghala.R.id.action_otpVerificationFragment2_to_setupAccountFragment2)
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun toggleLoading(displayLoading: Boolean) {
        if (displayLoading) {
            binding.pbOtpVerification.visibility = View.VISIBLE
            binding.verifyButton.isEnabled = false
        } else {
            binding.pbOtpVerification.visibility = View.GONE
            binding.verifyButton.isEnabled = true
        }
    }

}