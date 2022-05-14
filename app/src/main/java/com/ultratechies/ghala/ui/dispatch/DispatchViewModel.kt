package com.ultratechies.ghala.ui.dispatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.responses.deliverynotes.FetchDeliveryNotesResponseItem
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.DeliveryNotesRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DispatchViewModel @Inject constructor(private val deliveryNotesRepository: DeliveryNotesRepository) : ViewModel() {

    private val _fetchDeliveryNotes = MutableSharedFlow<List<FetchDeliveryNotesResponseItem>>()
    val fetchDeliveryNotes = _fetchDeliveryNotes.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    fun getFetchDeliveryNotes(){
        viewModelScope.launch {
            val fetchDeliveryNotesResponse = deliveryNotesRepository.fetchDeliveryNotes()
            when(fetchDeliveryNotesResponse){
                is APIResource.Success ->{
                    _fetchDeliveryNotes.emit(fetchDeliveryNotesResponse.value)
                }
                is APIResource.Loading ->{

                }
                is APIResource.Error ->{
                    _errorMessage.emit(parseErrors(fetchDeliveryNotesResponse))
                }
            }
        }
    }

}