package com.ultratechies.ghala.ui.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.requests.auth.CheckUserExistsRequest
import com.ultratechies.ghala.data.models.requests.auth.GetOTPRequest
import com.ultratechies.ghala.data.models.requests.user.CreateUserRequest
import com.ultratechies.ghala.data.models.requests.user.UpdateUserRequest
import com.ultratechies.ghala.data.models.requests.user.VerifyUserRequest
import com.ultratechies.ghala.data.models.responses.auth.CheckUserExistsResponse
import com.ultratechies.ghala.data.models.responses.auth.GetOTPResponse
import com.ultratechies.ghala.data.models.responses.auth.LoginResponse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.domain.models.UserModel
import com.ultratechies.ghala.domain.repository.UserRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(val userRepository: UserRepository) : ViewModel() {

    private val _userExists = MutableSharedFlow<CheckUserExistsResponse>()
    val userExists = _userExists.asSharedFlow()

    private val _createUser = MutableSharedFlow<UserModel>()
    val createUser = _createUser.asSharedFlow()

    private val _verifyUser = MutableSharedFlow<Boolean>()
    val verifyUser = _verifyUser.asSharedFlow()

    private val _updateUser = MutableSharedFlow<Any>()
    val updateUser = _updateUser.asSharedFlow()

    private val _getOTP = MutableSharedFlow<GetOTPResponse>()
    val getOTP = _getOTP.asSharedFlow()

    private val _otp = MutableStateFlow("")
    private val _otpResponse = MutableSharedFlow<Boolean>()
    val otpListener = _otpResponse.asSharedFlow()


    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _user = MutableStateFlow(
        UserModel(
            assignedWarehouse = 0,
            email = "",
            firstName = "",
            id = 0,
            lastName = "",
            password = "",
            phoneNumber = "",
            profilePhoto = listOf(),
            role = ""
        )
    )
    val user = _user.asStateFlow()

    private val _loggedInId = MutableStateFlow(-1)

    fun checkUserExists(checkUserExistsRequest: CheckUserExistsRequest) {
        viewModelScope.launch {
            val userExistsResponse = userRepository.getUserExists(checkUserExistsRequest)
            when (userExistsResponse) {
                is APIResource.Success -> {
                    _userExists.emit(userExistsResponse.value)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    _errorMessage.emit(parseErrors(userExistsResponse))
                }
            }

        }
    }

    fun fetchOtp(getOTPRequest: GetOTPRequest) {
        viewModelScope.launch {
            val otpResponse = userRepository.getOTP(getOTPRequest)
            when (otpResponse) {
                is APIResource.Success -> {
                    _otp.value = otpResponse.value.otp
                    _getOTP.emit(otpResponse.value)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    _errorMessage.emit(parseErrors(otpResponse))
                }

            }
        }

    }

    fun setPhoneNumber(phone: String) {
        _phoneNumber.value = phone
    }

    fun createUser(createUserRequest: CreateUserRequest) {
        viewModelScope.launch {
            val createUserResponse = userRepository.createUser(createUserRequest)
            when (createUserResponse) {
                is APIResource.Success -> {
                    _user.value = createUserResponse.value

                    _loggedInId.value = createUserResponse.value.id
                    _createUser.emit(createUserResponse.value)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    _errorMessage.emit(parseErrors(createUserResponse))
                }
            }
        }
    }

    fun verifyUser(verifyUserRequest: VerifyUserRequest) {
        viewModelScope.launch {
            val verifyUserResponse = userRepository.verifyUser(verifyUserRequest)
            when (verifyUserResponse) {
                is APIResource.Success -> {
                    _verifyUser.emit(verifyUserResponse.value)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    _errorMessage.emit(parseErrors(verifyUserResponse))
                }

            }
        }
    }

  /*  fun getUserById(id: Int?) {
        viewModelScope.launch {
            val getUserByIdResponse = userRepository.getUserById(id!!)
            when (getUserByIdResponse) {
                is APIResource.Success -> {
                    _getUserById.emit(getUserByIdResponse.value)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    _errorMessage.emit(parseErrors(getUserByIdResponse))
                }
            }
        }
    }*/

    fun resendOtp() {
        val phoneNumber = _phoneNumber.value
        val getOTPRequest = GetOTPRequest(
            email = null,
            name = null,
            phoneNumber = phoneNumber
        )

        fetchOtp(getOTPRequest)
    }

    fun updateUser(updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            val updateUser = userRepository.updateUser(updateUserRequest)
            when (updateUser) {
                is APIResource.Success -> {
                    _updateUser.emit(updateUser.value)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    _errorMessage.emit(parseErrors(updateUser))
                }
            }
        }
    }

    fun validateOtp(enteredOtp: String) {
        viewModelScope.launch {
            // get the otp
            val otp = _otp.value
            // check if its available
            if (otp == "") {
                _errorMessage.emit("Unable to get sent OTP")
            } else {
                // compare it with the user entered text
                if (otp == enteredOtp) {
                    _otpResponse.emit(true)
                } else {
                    _otpResponse.emit(false)
                    _errorMessage.emit("Otp do not match")
                }
            }

        }
    }
}