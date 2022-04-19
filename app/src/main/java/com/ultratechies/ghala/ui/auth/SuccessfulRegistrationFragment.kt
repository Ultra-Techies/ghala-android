package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ultratechies.ghala.R
import com.ultratechies.ghala.databinding.FragmentSuccessfulRegistrationBinding
import com.ultratechies.ghala.ui.auth.model.RegistrationUserDetails
import com.ultratechies.ghala.ui.auth.viewmodels.SetupAccountViewModel
import com.ultratechies.ghala.utils.validateEmail

class SuccessfulRegistrationFragment : Fragment() {
    private lateinit var binding: FragmentSuccessfulRegistrationBinding
    private val setupAccountViewModel: SetupAccountViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuccessfulRegistrationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUserData()
        editFields()
        validateFields()
    }

    private fun setUpUserData() {
        setupAccountViewModel.userRegistrationUserDetails.observe(viewLifecycleOwner) { data ->
            setDataToUi(data)
        }
    }

    fun editFields() {
        binding.apply {
            editButton.setOnClickListener {
                editTextAccountHolder.isEnabled = true
                editTextAccountHolder.requestFocus()
            }
            emailAddressEditButton.setOnClickListener {
                editTextAccountEmailAddress.isEnabled = true
                editTextAccountEmailAddress.requestFocus()
            }
            editWarehouseName.setOnClickListener {
                editTextWarehouseDetails.isEnabled = true
                editTextWarehouseDetails.requestFocus()
            }
            editUserRole.setOnClickListener {
                editTextUserRole.isEnabled = true
                editTextUserRole.requestFocus()
            }
            editUserRole.setOnClickListener {
                editTextUserRole.isEnabled = true
                editTextUserRole.requestFocus()
            }

        }
    }

    private fun validateFields() {
        binding.apply {
            registrationConfirmButton.setOnClickListener {

                pbRegistrationSuccessful.visibility = View.VISIBLE
                registrationConfirmButton.isEnabled = false

                if (editTextAccountHolder.text.trim().isNullOrEmpty()) {
                    editTextAccountHolder.error = "Please Enter Name "
                    pbRegistrationSuccessful.visibility = View.GONE
                    registrationConfirmButton.isEnabled = true
                    return@setOnClickListener
                }
                if (editTextAccountEmailAddress.text.trim().isNullOrEmpty()) {
                    editTextAccountEmailAddress.error = "Please enter email address"
                    pbRegistrationSuccessful.visibility = View.GONE
                    registrationConfirmButton.isEnabled = true
                    return@setOnClickListener
                }
                if (!validateEmail((binding.editTextAccountEmailAddress.text.trim().toString()))) {
                    editTextAccountEmailAddress.error = "Enter a valid email"
                    pbRegistrationSuccessful.visibility = View.GONE
                    registrationConfirmButton.isEnabled = true
                }
                if (editTextWarehouseDetails.text.trim().isNullOrEmpty()) {
                    editTextWarehouseDetails.error = "Please enter warehouse name"
                    pbRegistrationSuccessful.visibility = View.GONE
                    registrationConfirmButton.isEnabled = true
                    return@setOnClickListener
                }
                if (editTextUserRole.text.trim().isNullOrEmpty()) {
                    editTextUserRole.error = "Please Enter a role"
                    pbRegistrationSuccessful.visibility = View.GONE
                    registrationConfirmButton.isEnabled = true
                    return@setOnClickListener
                }
                findNavController().navigate(R.id.mainActivity)
            }
        }
    }

    private fun setDataToUi(data: RegistrationUserDetails) {
        binding.apply {
            editTextAccountHolder.setText(data.firstName + " " + data.secondName)
            editTextAccountEmailAddress.setText(data.email)
            editTextWarehouseDetails.setText(data.warehouse)
            editTextUserRole.setText(data.role)
        }
    }


}