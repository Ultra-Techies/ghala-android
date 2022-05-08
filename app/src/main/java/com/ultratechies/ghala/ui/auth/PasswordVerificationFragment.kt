package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.poovam.pinedittextfield.PinField
import com.poovam.pinedittextfield.SquarePinField
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.requests.user.VerifyUserRequest
import com.ultratechies.ghala.databinding.FragmentPasswordVerificationBinding
import com.ultratechies.ghala.ui.auth.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordVerificationFragment : Fragment() {
    private lateinit var binding: FragmentPasswordVerificationBinding

    private val userViewModel: UserViewModel by activityViewModels()

    private var phoneNumber: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPhoneNumber()
        verifyPin()
        getPin()
        verifyPinListener()
        verifyPinErrorListener()

    }

    private fun getPhoneNumber() {
        lifecycleScope.launchWhenCreated {
            userViewModel.phoneNumber.collectLatest {
                phoneNumber = it
            }
        }
    }

    private fun verifyPinErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.errorMessage.collect {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun verifyPinListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.verifyUser.collect { isUserVerified ->
                    if (isUserVerified) {
                        findNavController().navigate(R.id.action_passwordVerificationFragment_to_mainActivity)
                    } else {
                        binding.pbOtpVerification.visibility = View.GONE
                        binding.loginButton.isEnabled = true
                        Snackbar.make(binding.root, "pins do not match", Snackbar.LENGTH_SHORT)
                            .show()
                        return@collect
                    }

                }
            }
        }
    }

    private fun verifyPin() {
        binding.loginButton.setOnClickListener {
            binding.pbOtpVerification.visibility = View.VISIBLE
            binding.loginButton.isEnabled = false

            if (binding.squareField.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please Enter PIN", Toast.LENGTH_SHORT).show()
                binding.pbOtpVerification.visibility = View.GONE
                binding.loginButton.isEnabled = true
                return@setOnClickListener
            }

            val verifyUser = VerifyUserRequest(
                password = binding.squareField.text.toString(),
                phoneNumber = phoneNumber!!

            )
            verifyExistingUser(verifyUser)
        }
    }

    private fun verifyExistingUser(verifyUserRequest: VerifyUserRequest) {
        userViewModel.verifyUser(verifyUserRequest)
    }

    private fun getPin() {
        val squarePinField: SquarePinField = binding.squareField
        squarePinField.onTextCompleteListener = object : PinField.OnTextCompleteListener {
            override fun onTextComplete(enteredText: String): Boolean {
                Toast.makeText(context, enteredText, Toast.LENGTH_SHORT).show()

                return true
            }
        }
    }
}