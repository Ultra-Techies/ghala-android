package com.ultratechies.ghala.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ultratechies.ghala.R
import com.ultratechies.ghala.databinding.FragmentSetupAccountBinding
import com.ultratechies.ghala.utils.validateEmail


class SetupAccountFragment : Fragment() {
    private lateinit var binding: FragmentSetupAccountBinding
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

        binding.setUpNextButton.setOnClickListener {
            binding.pbSetupVerification.visibility = View.VISIBLE
            binding.setUpNextButton.isEnabled = false

            if (binding.editTextTextFirstName.text.trim().isNullOrEmpty()){
                binding.editTextTextFirstName.error ="Please Enter Name "
                binding.pbSetupVerification.visibility = View.GONE
                binding.setUpNextButton.isEnabled = true
                return@setOnClickListener
            }
            if (binding.editTextTextFirstName.text.trim().length <=2 ){
                binding.editTextTextFirstName.error = "Please enter a name with more than 2 characters"
                binding.pbSetupVerification.visibility = View.GONE
                binding.setUpNextButton.isEnabled = true
                return@setOnClickListener
            }
            if (binding.editTextTextSecondName.text.trim().isNullOrEmpty()){
                binding.editTextTextSecondName.error ="Please Enter Name "
                binding.pbSetupVerification.visibility = View.GONE
                binding.setUpNextButton.isEnabled = true
                return@setOnClickListener
            }
            if (binding.editTextTextSecondName.text.trim().length <=2 ){
                binding.editTextTextSecondName.error = "Please enter a name with more than 2 characters"
                binding.pbSetupVerification.visibility = View.GONE
                binding.setUpNextButton.isEnabled = true
                return@setOnClickListener
            }
            if (binding.editTextEmailAddress.text.trim().isNullOrEmpty()){
                binding.editTextEmailAddress.error = "Please Enter an email address"
                binding.pbSetupVerification.visibility = View.GONE
                binding.setUpNextButton.isEnabled = true
                return@setOnClickListener
            }
            if (!validateEmail(binding.editTextEmailAddress.text.trim().toString())) {
                binding.editTextEmailAddress.error = "Please Enter a valid Email"
                binding.pbSetupVerification.visibility = View.GONE
                binding.setUpNextButton.isEnabled = true
                return@setOnClickListener
            }
            if (binding.warehouseSpinner.selectedItemPosition == 0){
                Toast.makeText(requireContext(),"Please Select a warehouse",Toast.LENGTH_SHORT).show()
                binding.pbSetupVerification.visibility = View.GONE
                binding.setUpNextButton.isEnabled = true
                return@setOnClickListener
            }
            if (binding.departmentSpinner.selectedItemPosition == 0){
                Toast.makeText(requireContext(),"Please Select a Department",Toast.LENGTH_SHORT).show()
                binding.pbSetupVerification.visibility = View.GONE
                binding.setUpNextButton.isEnabled = true
                return@setOnClickListener
            }
            if (binding.roleSpinner.selectedItemPosition == 0){
                Toast.makeText(requireContext(),"Please Select a Role",Toast.LENGTH_SHORT).show()
                binding.pbSetupVerification.visibility = View.GONE
                binding.setUpNextButton.isEnabled = true
                return@setOnClickListener
            }

        }

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
}