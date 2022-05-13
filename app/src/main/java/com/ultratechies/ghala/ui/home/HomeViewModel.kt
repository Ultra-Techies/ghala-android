package com.ultratechies.ghala.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.responses.home.HomeStatsResponse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.HomeStatsRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val homeStatsRepo: HomeStatsRepository) : ViewModel() {
    private val _stats = MutableSharedFlow<HomeStatsResponse>()
    val stats = _stats.asSharedFlow()

    private val _errorResponse = MutableSharedFlow<String>()
    val errorResponse = _errorResponse.asSharedFlow()

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    fun getStats(id: Int) {
        viewModelScope.launch {
            val homeStatsResponse = homeStatsRepo.getStats(id)
            when(homeStatsResponse){
                is APIResource.Success->{
                    _stats.emit(homeStatsResponse.value)
                }
                is APIResource.Loading ->{
                    _isLoading.emit(true)
                }
                is APIResource.Error ->{
                    _errorResponse.emit(parseErrors(homeStatsResponse))
                }
            }
        }
    }
}