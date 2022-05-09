package com.ultratechies.ghala.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ultratechies.ghala.databinding.SettingsFragmentBinding
import com.ultratechies.ghala.domain.models.UserModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: SettingsFragmentBinding

    private lateinit var viewModel: SettingsViewModel

    private val userModel: UserModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editFields()
        validateFields()
        getUserByIdListener()
        getUserByIdErrorListener()
        updateUserListener()
        updateUserErrorListener()

    }

    private fun toggleLoading(displayLoading: Boolean){
        if (displayLoading){

        }
        else{

        }
    }

    private fun validateFields() {
        TODO("Not yet implemented")
    }

    private fun editFields() {
        TODO("Not yet implemented")
    }

    private fun updateUserErrorListener() {
        TODO("Not yet implemented")
    }

    private fun updateUserListener() {
        TODO("Not yet implemented")
    }

    private fun getUserByIdErrorListener() {
        TODO("Not yet implemented")
    }

    private fun getUserByIdListener() {
        TODO("Not yet implemented")
    }

}