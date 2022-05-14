package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.R
import com.ultratechies.ghala.data.models.requests.user.CreateUserRequest
import com.ultratechies.ghala.data.models.responses.warehouses.Warehouse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.databinding.FragmentSetupAccountBinding
import com.ultratechies.ghala.ui.auth.viewmodels.UserViewModel
import com.ultratechies.ghala.ui.warehouses.WarehousesViewModel
import com.ultratechies.ghala.utils.gone
import com.ultratechies.ghala.utils.validateEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetupAccountFragment : Fragment() {
    private lateinit var binding: FragmentSetupAccountBinding

    private val userViewmodel: UserViewModel by activityViewModels()
    private val warehouseViewModel: WarehousesViewModel by viewModels()

    private var phoneNumber: String? = null
    private val wareHouses = mutableListOf<Warehouse>()

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
        fetchWareHouses()
        getPhoneNumber()
        validateUserInputFields()
        fetchErrorListener()
        registerUserListener()
        registerUserErrorListener()



        warehouseViewModel.fetchWarehouses()
        binding.toolbarWelcome.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_setupAccountFragment2_to_otpVerificationFragment2)
        }
    }
    private fun setUpToolbar() {
        (requireActivity() as AuthActivity).setSupportActionBar(binding.toolbarWelcome)
        binding.toolbarWelcome.showOverflowMenu()
        (requireActivity() as AuthActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AuthActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (requireActivity() as AuthActivity).supportActionBar?.title = "Setup Account"
    }
    private fun fetchWareHouses() {
        warehouseViewModel.warehouses.observe(viewLifecycleOwner) { state ->
            when (state) {
                is APIResource.Success -> {
                    binding.apply {
                        progressBarWarehouses.gone()

                        wareHouses.apply {
                            clear()
                            addAll(state.value)
                        }

                        val adapter = ArrayAdapter(
                            requireActivity(),
                            android.R.layout.simple_spinner_dropdown_item,
                            wareHouses.map { it.name })
                        binding.warehouseSpinner.adapter = adapter
                    }
                }
                else -> {

                }
            }
        }
    }

    private fun getPhoneNumber() {
        lifecycleScope.launchWhenCreated {
            userViewmodel.phoneNumber.collectLatest {
                phoneNumber = it
            }
        }
    }


    private fun validateUserInputFields() {
        binding.apply {
            setUpNextButton.setOnClickListener {

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
              /*  if (warehouseSpinner.selectedItemPosition == 0) {
                    Toast.makeText(
                        requireContext(),
                        "Please Select a warehouse",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }*/
                if (editTextTextPin.text.trim().isNullOrEmpty()) {
                    editTextTextPin.error = "Please Enter you Pin "
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }
                if (editTextTextPin.text.trim() != editTextTextRepeatPin.text.trim()) {
                    editTextTextPin.error = "Pins do not match "
                    pbSetupVerification.visibility = View.GONE
                    setUpNextButton.isEnabled = true
                    return@setOnClickListener
                }


                val firstName = editTextTextSecondName.text.trim().toString()
                val secondName = editTextTextSecondName.text.trim().toString()
                val email = editTextEmailAddress.text.trim().toString()
  /*              val warehouse = warehouseSpinner.selectedItem.toString()*/
                val password = binding.editTextTextPin.text.trim().toString()


                val userDetails = CreateUserRequest(
                    assignedWarehouse = wareHouses.find { it.name == binding.warehouseSpinner.selectedItem.toString() }!!.id,
                    email = email,
                    firstName = firstName,
                    lastName = secondName,
                    password = password,
                    phoneNumber = phoneNumber,
                    profilePhoto = listOf()
                )
                registerUser(createUserRequest = userDetails)
            }
        }
    }

    private fun registerUser(createUserRequest: CreateUserRequest) {
        toggleLoading(true)
        userViewmodel.createUser(createUserRequest)
    }


    private fun fetchErrorListener() {
        warehouseViewModel.errorMessage.observe(viewLifecycleOwner){
            Snackbar.make(binding.root,it,Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun registerUserListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewmodel.createUser.collect {
                    toggleLoading(true)
                    findNavController().navigate(R.id.action_setupAccountFragment2_to_successfulRegistrationFragment2)
                }
            }
        }
    }
    private fun registerUserErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewmodel.errorMessage.collect {
                    toggleLoading(false)
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun toggleLoading(displayLoading: Boolean) {
        if (displayLoading) {
            binding.pbSetupVerification.visibility = View.VISIBLE
            binding.pbSetupVerification.isEnabled = false
        } else {
            binding.pbSetupVerification.visibility = View.GONE
            binding.pbSetupVerification.isEnabled = true
        }
    }
}
