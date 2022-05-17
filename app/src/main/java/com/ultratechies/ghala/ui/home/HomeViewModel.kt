package com.ultratechies.ghala.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.responses.home.HomeStatsResponse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.HomeStatsRepository
import com.ultratechies.ghala.domain.models.UserModel
import com.ultratechies.ghala.domain.repository.UserRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    val userRepository: UserRepository,
    val homeStatsRepo: HomeStatsRepository
) : ViewModel() {

    private val _getUserById = MutableSharedFlow<UserModel>()
    val getUserById = _getUserById.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _stats = MutableSharedFlow<HomeStatsResponse>()
    val stats = _stats.asSharedFlow()

    private val _errorResponse = MutableSharedFlow<String>()
    val errorResponse = _errorResponse.asSharedFlow()

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    private val _unAuthorizedError = MutableSharedFlow<Boolean>()
    val unAuthorizedError = _unAuthorizedError.asSharedFlow()


    fun getUserById() {
        viewModelScope.launch {
            when (val response = userRepository.getUserById()) {
                is APIResource.Success -> {
                    _getUserById.emit(response.value)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    if (response.errorCode == 403)
                        _unAuthorizedError.emit(true)
                    else
                        _errorMessage.emit(parseErrors(response))
                }
            }
        }
    }

    fun getStats(id: Int) {
        viewModelScope.launch {
            val homeStatsResponse = homeStatsRepo.getStats(id)
            when (homeStatsResponse) {
                is APIResource.Success -> {
                    _stats.emit(homeStatsResponse.value)
                }
                is APIResource.Loading -> {
                    _isLoading.emit(true)
                }
                is APIResource.Error -> {
                    if (homeStatsResponse.errorCode == 403)
                        _unAuthorizedError.emit(true)
                    else
                        _errorMessage.emit(parseErrors(homeStatsResponse))
                }
            }
        }
    }
}