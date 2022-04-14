package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ultratechies.ghala.R
import com.ultratechies.ghala.databinding.FragmentSuccessfulRegistrationBinding
import com.ultratechies.ghala.utils.validateEmail

class SuccessfulRegistrationFragment : Fragment() {
    private lateinit var binding: FragmentSuccessfulRegistrationBinding

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

        var userName = "Malcolm Maina"
        binding.editTextAccountHolder.setText(userName)

        var userEmailAddress = "malcolm@gmail.com"
        binding.editTextAccountEmailAddress.setText(userEmailAddress)

        var userWarehouse = "Ruiru WH"
        binding.editTextWarehouseDetails.setText(userWarehouse)

        var userRole = "Dispatch Associate"
        binding.editTextUserRole.setText(userRole)

        binding.editButton.setOnClickListener {
            binding.editTextAccountHolder.isEnabled = true
            binding.editTextAccountHolder.requestFocus()
        }
        binding.emailAddressEditButton.setOnClickListener {
            binding.editTextAccountEmailAddress.isEnabled = true
            binding.editTextAccountEmailAddress.requestFocus()
        }
        binding.editWarehouseName.setOnClickListener {
            binding.editTextWarehouseDetails.isEnabled = true
            binding.editTextWarehouseDetails.requestFocus()
        }
        binding.editUserRole.setOnClickListener {
            binding.editTextUserRole.isEnabled = true
            binding.editTextUserRole.requestFocus()
        }

        binding.registrationConfirmButton.setOnClickListener {

            binding.pbRegistrationSuccessful.visibility = View.VISIBLE
            binding.registrationConfirmButton.isEnabled = false

            if (binding.editTextAccountHolder.text.trim().isNullOrEmpty()) {
                binding.editTextAccountHolder.error = "Please Enter Name "
                binding.pbRegistrationSuccessful.visibility = View.GONE
                binding.registrationConfirmButton.isEnabled = true
                return@setOnClickListener
            }
            if (binding.editTextAccountEmailAddress.text.trim().isNullOrEmpty()) {
                binding.editTextAccountEmailAddress.error = "Please enter email address"
                binding.pbRegistrationSuccessful.visibility = View.GONE
                binding.registrationConfirmButton.isEnabled = true
                return@setOnClickListener
            }
            if (!validateEmail((binding.editTextAccountEmailAddress.text.trim().toString()))) {
                binding.editTextAccountEmailAddress.error = "Enter a valid email"
                binding.pbRegistrationSuccessful.visibility = View.GONE
                binding.registrationConfirmButton.isEnabled = true
            }
            if (binding.editTextWarehouseDetails.text.trim().isNullOrEmpty()) {
                binding.editTextWarehouseDetails.error = "Please enter warehouse name"
                binding.pbRegistrationSuccessful.visibility = View.GONE
                binding.registrationConfirmButton.isEnabled = true
                return@setOnClickListener
            }
            if (binding.editTextUserRole.text.trim().isNullOrEmpty()) {
                binding.editTextUserRole.error = "Please Enter a role"
                binding.pbRegistrationSuccessful.visibility = View.GONE
                binding.registrationConfirmButton.isEnabled = true
                return@setOnClickListener
            }
            findNavController().navigate(R.id.mainActivity)
        }
    }

}