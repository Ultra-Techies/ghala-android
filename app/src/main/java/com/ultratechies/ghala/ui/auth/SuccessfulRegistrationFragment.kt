package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.requests.user.UpdateUserRequest
import com.ultratechies.ghala.databinding.FragmentSuccessfulRegistrationBinding
import com.ultratechies.ghala.domain.models.UserModel
import com.ultratechies.ghala.ui.auth.viewmodels.UserViewModel
import com.ultratechies.ghala.utils.validateEmail
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SuccessfulRegistrationFragment : Fragment() {
    private lateinit var binding: FragmentSuccessfulRegistrationBinding

    private val userViewModel: UserViewModel by activityViewModels()

    private var userModel: UserModel? = null

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
        /*   setUpUserData()*/
        editFields()
        validateFields()
        getUserByIdListener()
        getUserByIdErrorListener()
        updateUserListener()
        updateUserErrorListener()
    }

    private fun toggleLoading(displayLoading: Boolean) {
        if (displayLoading) {
            binding.pbRegistrationSuccessful.visibility = View.VISIBLE
            binding.pbRegistrationSuccessful.isEnabled = false
        } else {
            binding.pbRegistrationSuccessful.visibility = View.GONE
            binding.pbRegistrationSuccessful.isEnabled = true
        }
    }

    private fun updateUserErrorListener() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.errorMessage.collectLatest {
                    toggleLoading(false)
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserListener() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.updateUser.collect {
                    toggleLoading(false)
                    findNavController().navigate(R.id.mainActivity)
                }
            }
        }
    }

    private fun updateUser(updateUserRequest: UpdateUserRequest) {
        toggleLoading(true)
        userViewModel.updateUser(updateUserRequest)
    }


    private fun getUserByIdErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.errorMessage.collect {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getUserByIdListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.user.collect {
                    userModel = it
                    binding.apply {
                        editTextAccountHolder.setText(it.firstName)
                        editTextSecondName.setText(it.lastName)
                        editTextAccountEmailAddress.setText(it.email)
                        editTextUserRole.setText(it.role)
                        /*   editTextWarehouseDetails.setText(it.assignedWarehouse)*/
                        /* editTextWarehouseDetails.setText(it.assignedWarehouse)*/
                    }
                }
            }
        }
    }

    private fun editFields() {
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
                if (editTextAccountHolder.text.trim().isEmpty()) {
                    editTextAccountHolder.error = "Please Enter First Name "
                    return@setOnClickListener
                }
                if (editTextSecondName.text.trim().isEmpty()) {
                    editTextSecondName.error = "Please Enter Second Name "
                    return@setOnClickListener
                }
                if (editTextAccountEmailAddress.text.trim().isEmpty()) {
                    editTextAccountEmailAddress.error = "Please enter email address"
                    toggleLoading(true)
                    return@setOnClickListener
                }
                if (!validateEmail((binding.editTextAccountEmailAddress.text.trim().toString()))) {
                    editTextAccountEmailAddress.error = "Enter a valid email"
                    return@setOnClickListener
                }

                // check if data is the same
                if ((userModel!!.firstName + " " + userModel!!.lastName) == editTextAccountHolder.text.trim()
                        .toString()
                    && userModel!!.email == editTextAccountEmailAddress.text.trim().toString()
                ) {
                    findNavController().navigate(R.id.mainActivity)
                } else {
                    toggleLoading(true)
                    val updateUserRequest = UpdateUserRequest(
                        id = userModel!!.id,
                        firstName = editTextAccountHolder.text.toString(),
                        lastName = editTextSecondName.text.toString(),
                        email = editTextAccountEmailAddress.text.toString(),

                        )
                    updateUser(updateUserRequest)
                }

            }
        }
    }
}