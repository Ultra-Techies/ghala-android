package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ultratechies.ghala.R
import com.ultratechies.ghala.databinding.FragmentSetupAccountBinding
import com.ultratechies.ghala.ui.auth.model.RegistrationUserDetails
import com.ultratechies.ghala.ui.auth.viewmodels.SetupAccountViewModel
import com.ultratechies.ghala.utils.validateEmail


class SetupAccountFragment : Fragment() {
    private lateinit var binding: FragmentSetupAccountBinding
    private val setUpAccountViewModel: SetupAccountViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetupAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar()
        validateUserInputFields()

        binding.toolbarWelcome.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_setupAccountFragment2_to_otpVerificationFragment2)
        }
    }

    private fun validateUserInputFields() {
        binding.apply {
            setUpNextButton.setOnClickListener {
                pbSetupVerification.visibility = View.VISIBLE
                setUpNextButton.isEnabled = false

                if (editTextTextFirstName.text.trim().isNullOrEmpty()) {
                    editTextTextFirstName.error = "Please Enter Name "
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }
                if (editTextTextFirstName.text.trim().length <= 2) {
                    editTextTextFirstName.error =
                        "Please enter a name with more than 2 characters"
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }
                if (editTextTextSecondName.text.trim().isNullOrEmpty()) {
                    editTextTextSecondName.error = "Please Enter Name "
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }
                if (editTextTextSecondName.text.trim().length <= 2) {
                    editTextTextSecondName.error =
                        "Please enter a name with more than 2 characters"
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }
                if (editTextEmailAddress.text.trim().isNullOrEmpty()) {
                    editTextEmailAddress.error = "Please Enter an email address"
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }
                if (!validateEmail(editTextEmailAddress.text.trim().toString())) {
                    editTextEmailAddress.error = "Please Enter a valid Email"
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }
                if (warehouseSpinner.selectedItemPosition == 0) {
                    Toast.makeText(
                        requireContext(),
                        "Please Select a warehouse",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }
                if (departmentSpinner.selectedItemPosition == 0) {
                    Toast.makeText(
                        requireContext(),
                        "Please Select a Department",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }
                if (roleSpinner.selectedItemPosition == 0) {
                    Toast.makeText(requireContext(), "Please Select a Role", Toast.LENGTH_SHORT)
                        .show()
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }

                val firstName = editTextTextSecondName.text.trim().toString()
                val secondName = editTextTextSecondName.text.trim().toString()
                val email = editTextEmailAddress.text.trim().toString()
                val warehouse = warehouseSpinner.selectedItem.toString()
                val department = departmentSpinner.selectedItem.toString()
                val role = roleSpinner.selectedItem.toString()

                val userDetails = RegistrationUserDetails(
                    firstName = firstName,
                    secondName = secondName,
                    email = email,
                    warehouse = warehouse,
                    department = department,
                    role = role

                )
                Log.d("--->", userDetails.toString())

                setUpAccountViewModel.setData(userDetails)

                findNavController().navigate(R.id.action_setupAccountFragment2_to_successfulRegistrationFragment2)
            }
        }
    }

    private fun setUpToolbar() {
        (requireActivity() as AuthActivity).setSupportActionBar(binding.toolbarWelcome)
        binding.toolbarWelcome.showOverflowMenu()
        (requireActivity() as AuthActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AuthActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (requireActivity() as AuthActivity).supportActionBar?.title = "Setup Account"
    }
}