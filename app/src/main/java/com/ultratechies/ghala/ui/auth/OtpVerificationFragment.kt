package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.poovam.pinedittextfield.PinField
import com.poovam.pinedittextfield.SquarePinField
import com.ultratechies.ghala.databinding.FragmentOtpVerificationBinding


class OtpVerificationFragment: Fragment() {
    private lateinit var binding: FragmentOtpVerificationBinding

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

        getOTP()

        binding.tvResend.setOnClickListener {

        }

        binding.verifyButton.setOnClickListener {
            binding.pbOtpVerification.visibility = View.VISIBLE
            binding.verifyButton.isEnabled = false

            if (binding.squareField.text.isNullOrEmpty()){
                Toast.makeText(requireContext(),"Please Enter OTP",Toast.LENGTH_SHORT).show()
                binding.pbOtpVerification.visibility = View.GONE
                binding.verifyButton.isEnabled = true
                return@setOnClickListener
            }
            findNavController().navigate(com.ultratechies.ghala.R.id.action_otpVerificationFragment2_to_setupAccountFragment2)
        }
    }

    private fun getOTP(){
        val squarePinField: SquarePinField = binding.squareField
        squarePinField.onTextCompleteListener = object : PinField.OnTextCompleteListener {
            override fun onTextComplete(enteredText: String): Boolean {
                Toast.makeText(context, enteredText, Toast.LENGTH_SHORT).show()
                return true // Return false to keep the keyboard open else return true to close the keyboard
            }
        }
    }
}