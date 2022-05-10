package com.ultratechies.ghala.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.data.models.requests.user.UpdateUserRequest
import com.ultratechies.ghala.data.models.responses.Warehouse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.databinding.SettingsFragmentBinding
import com.ultratechies.ghala.domain.models.UserModel
import com.ultratechies.ghala.ui.warehouses.WarehousesViewModel
import com.ultratechies.ghala.utils.gone
import com.ultratechies.ghala.utils.validateEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: SettingsFragmentBinding

    private val viewModel: SettingsViewModel by viewModels()
    private val warehouseViewModel: WarehousesViewModel by viewModels()
    private var userModel: UserModel? = null
    private val wareHouses = mutableListOf<Warehouse>()


    @Inject
    lateinit var appDatasource: AppDatasource

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("on create",wareHouses.toString())

        validateFields()
        displayUserData()
        fetchWareHouses()
        updateUserListener()
        updateUserErrorListener()

        warehouseViewModel.fetchWarehouses()

    }


    private fun displayUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            appDatasource.getUserFromPreferencesStore().collectLatest { user ->
              binding.apply {
                  userModel = user
                  editTextUpdateFirstName.setText(user.firstName)
                  editTextUpdateSecondName.setText(user.lastName)
                  editTextUpdateEmailAddress.setText(user.email)
                  editTextUpdatePin.setText(user.password)
                  editTextUpdateRepeatPin.setText(user.password)
              }
            }
        }
    }

    private fun toggleLoading(displayLoading: Boolean) {
        if (displayLoading) {
            binding.pbSetupVerification.visibility = View.VISIBLE
            binding.updateUser.isEnabled = false
        } else {
            binding.pbSetupVerification.visibility = View.GONE
            binding.updateUser.isEnabled = true
        }
    }

    private fun fetchWareHouses() {
       warehouseViewModel.warehouses.observe(viewLifecycleOwner){state->
           when(state){
               is APIResource.Success ->{
                   binding.apply {
                       progressBarWarehouses.gone()
                       wareHouses.apply {
                           clear()
                           addAll(state.value)
                       }
                       val adapter = ArrayAdapter(
                           requireActivity(),
                           android.R.layout.simple_spinner_dropdown_item,
                           wareHouses.map { it.name }
                       )
                       binding.updateWarehouseSpinner.adapter = adapter
                       Log.d("---->", wareHouses.toString())
                   }
               }else->{

               }
           }
       }
    }

    private fun validateFields() {
        binding.apply {
            updateUser.setOnClickListener {
                if (editTextUpdateFirstName.text.trim().isEmpty()) {
                    editTextUpdateFirstName.error = "Please Enter First Name"
                    return@setOnClickListener
                }
                if (editTextUpdateFirstName.text.trim().length <= 2) {
                    editTextUpdateFirstName.error = "Enter a name with more than two words"
                    return@setOnClickListener
                }
                if (editTextUpdateSecondName.text.trim().length <= 2) {
                    editTextUpdateSecondName.error = "Enter a name with more than two words"
                }
                if (editTextUpdateSecondName.text.trim().isEmpty()) {
                    editTextUpdateSecondName.error = "Please Enter Second Name"
                    return@setOnClickListener
                }
                if (editTextUpdateEmailAddress.text.trim().isEmpty()) {
                    editTextUpdateEmailAddress.error = "Please Enter an email address"
                    return@setOnClickListener
                }
                if (!validateEmail(editTextUpdateEmailAddress.text.trim().toString())) {
                    editTextUpdateEmailAddress.error = "Please Enter a valid email"
                    return@setOnClickListener
                }

                if (userModel?.firstName == editTextUpdateFirstName.text.trim().toString()
                            && userModel?.lastName == editTextUpdateSecondName.text.trim().toString()
                            && userModel?.email == binding.editTextUpdateEmailAddress.text.toString() &&
                            userModel?.password == editTextUpdatePin.text.trim().toString()){

                    }else{
                        toggleLoading(true)
                    val updateUserRequest = UpdateUserRequest(
                    /*    assignedWarehouse = null,*/
                        email = editTextUpdateEmailAddress.text.trim().toString(),
                        firstName = editTextUpdateFirstName.text.trim().toString(),
                        id = userModel!!.id,
                        lastName = editTextUpdateSecondName.text.trim().toString(),
                        password = editTextUpdatePin.text.trim().toString(),
                        phoneNumber = null,
                        profilePhoto = listOf(),
                        role = null

                    )
                    updateUser(updateUserRequest)
                }

            }
        }
    }

    private fun updateUserErrorListener() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collectLatest {
                    toggleLoading(false)
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserListener() {
       lifecycleScope.launch {
           repeatOnLifecycle(Lifecycle.State.STARTED){
               viewModel.updateUser.collect{
                   toggleLoading(false)
                   Snackbar.make(binding.root,"Use Updated successfully",Snackbar.LENGTH_SHORT).show()
               }
           }
       }
    }
    private fun updateUser(updateUserRequest: UpdateUserRequest){
        toggleLoading(true)
        viewModel.updateUser(updateUserRequest)
    }





}