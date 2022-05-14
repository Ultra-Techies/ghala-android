package com.ultratechies.ghala.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.domain.models.UserModel
import com.ultratechies.ghala.domain.repository.UserRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val userRepository: UserRepository) : ViewModel() {

    private val _getUserById = MutableSharedFlow<UserModel>()
    val getUserById = _getUserById.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    fun getUserById() {
        viewModelScope.launch {
            when (val response = userRepository.getUserById()) {
                is APIResource.Success -> {
                    _getUserById.emit(response.value)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    _errorMessage.emit(parseErrors(response))
                }
            }
        }
    }


}