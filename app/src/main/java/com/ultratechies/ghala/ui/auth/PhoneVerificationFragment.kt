package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbb20.CountryCodePicker
import com.ultratechies.ghala.R
import com.ultratechies.ghala.databinding.FragmentPhoneVerificationBinding


class PhoneVerificationFragment : Fragment() {
    private lateinit var binding: FragmentPhoneVerificationBinding

    private lateinit var countryCodePicker: CountryCodePicker

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

        countryCodePicker = binding.ccp
        countryCodePicker.registerCarrierNumberEditText(binding.etPhoneNumber)

        binding.nextButton.setOnClickListener {
            binding.pbPhoneVerification.visibility = View.VISIBLE
            binding.nextButton.isEnabled = false

            if (!countryCodePicker.isValidFullNumber) {
                binding.etPhoneNumber.error = "Invalid phone number"
                binding.pbPhoneVerification.visibility = View.GONE
                binding.nextButton.isEnabled = true
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_phoneVerificationFragment2_to_otpVerificationFragment2)
        }


    }

}