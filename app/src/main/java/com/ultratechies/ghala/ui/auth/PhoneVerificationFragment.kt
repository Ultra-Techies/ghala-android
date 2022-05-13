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
import com.hbb20.CountryCodePicker
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.requests.auth.CheckUserExistsRequest
import com.ultratechies.ghala.data.models.requests.auth.GetOTPRequest
import com.ultratechies.ghala.databinding.FragmentPhoneVerificationBinding
import com.ultratechies.ghala.ui.auth.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PhoneVerificationFragment : Fragment() {
    private lateinit var binding: FragmentPhoneVerificationBinding

    private lateinit var countryCodePicker: CountryCodePicker

    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validatePhoneNumber()

        checkUserExistsListener()

        checkUserErrorListener()

        fetchOTPlistener()

        fetchOTPErrorListener()
    }

    private fun toggleLoading(displayLoading: Boolean) {
        if (displayLoading) {
            binding.pbPhoneVerification.visibility = View.VISIBLE
            binding.nextButton.isEnabled = false
        } else {
            binding.pbPhoneVerification.visibility = View.GONE
            binding.nextButton.isEnabled = true
        }
    }

    private fun validatePhoneNumber() {
        countryCodePicker = binding.ccp
        countryCodePicker.registerCarrierNumberEditText(binding.etPhoneNumber)

        binding.nextButton.setOnClickListener {
            toggleLoading(true)

            if (!countryCodePicker.isValidFullNumber) {
                binding.etPhoneNumber.error = "Invalid phone number"

                toggleLoading(false)
                return@setOnClickListener
            }
            val checkUserExists = CheckUserExistsRequest(
                phoneNumber = countryCodePicker.fullNumberWithPlus
            )

            viewModel.setPhoneNumber(countryCodePicker.fullNumberWithPlus)
            getUserExists(checkUserExists)
        }

    }

    private fun checkUserExistsListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userExists.collect {
                if (it.exists) {
                    toggleLoading(false)
                    findNavController().navigate(R.id.action_phoneVerificationFragment2_to_passwordVerificationFragment2)
                } else {
                    fetchOTP()
                }
            }
        }
    }

    private fun checkUserErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect {
                toggleLoading(false)
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    private fun getUserExists(checkUserExistsRequest: CheckUserExistsRequest) {
        viewModel.checkUserExists(checkUserExistsRequest)
    }

    private fun fetchOTPlistener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getOTP.collect {
                toggleLoading(false)
                findNavController().navigate(
                    R.id.action_phoneVerificationFragment2_to_otpVerificationFragment2
                )
            }
        }
    }

    private fun fetchOTPErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchOTP() {
        val getOTPRequest = GetOTPRequest(
            email = null,
            name = null,
            phoneNumber = countryCodePicker.fullNumberWithPlus
        )
        viewModel.fetchOtp(getOTPRequest)
    }

}