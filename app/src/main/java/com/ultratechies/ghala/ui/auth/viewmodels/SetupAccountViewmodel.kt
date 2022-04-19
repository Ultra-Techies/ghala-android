package com.ultratechies.ghala.ui.auth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ultratechies.ghala.ui.auth.model.RegistrationUserDetails

class SetupAccountViewModel : ViewModel() {

    private val _userRegistrationDetails = MutableLiveData<RegistrationUserDetails>()
    val userRegistrationUserDetails: LiveData<RegistrationUserDetails> get() = _userRegistrationDetails

    fun setData( registrationUserDetails: RegistrationUserDetails ) {
        _userRegistrationDetails.value = registrationUserDetails
    }
}