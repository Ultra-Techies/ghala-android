package com.ultratechies.ghala.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.requests.user.UpdateUserRequest
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
class SettingsViewModel @Inject constructor(val userRepository: UserRepository)  : ViewModel() {

    private val _updateUser = MutableSharedFlow<Any>()
    val updateUser = _updateUser.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _unAuthorizedError = MutableSharedFlow<Boolean>()
    val unAuthorizedError = _unAuthorizedError.asSharedFlow()

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

    fun updateUser(updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            when (  val updateUser = userRepository.updateUser(updateUserRequest)) {
                is APIResource.Success -> {
                    _updateUser.emit(updateUser.value)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    if (updateUser.errorCode == 403)
                        _unAuthorizedError.emit(true)
                    else
                        _errorMessage.emit(parseErrors(updateUser))
                }
            }
        }
    }

}