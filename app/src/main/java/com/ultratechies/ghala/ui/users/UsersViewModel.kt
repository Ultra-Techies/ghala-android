package com.ultratechies.ghala.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.responses.user.FetchAllUsersResponse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.domain.repository.UserRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UsersViewModel @Inject constructor(val usersRepository: UserRepository) : ViewModel() {

    private val _fetchAllUsers = MutableSharedFlow<List<FetchAllUsersResponse>>()
    val fetchAllUsersResponse = _fetchAllUsers.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    fun fetchAllUsers() {
        viewModelScope.launch {
            when (val fetchUserResponse = usersRepository.fetchAllUsers()) {
                is APIResource.Success -> {
                        _fetchAllUsers.emit(fetchUserResponse.value)
                }
                is APIResource.Error -> {
                    _errorMessage.emit(parseErrors(fetchUserResponse))
                }
                is APIResource.Loading -> {

                }
            }
        }
    }

}