package com.ultratechies.ghala.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.requests.deliverynotes.CreateDeliveryNoteRequest
import com.ultratechies.ghala.data.models.responses.deliverynotes.CreateDeliveryNoteResponse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.DeliveryNotesRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDeliveryNotesViewModel @Inject constructor(val deliveryNotesRepository: DeliveryNotesRepository) :ViewModel(){

    private val _createDeliveryNotes= MutableSharedFlow<CreateDeliveryNoteResponse>()
    val createDeliveryNotes = _createDeliveryNotes.asSharedFlow()

    private val _errorResponse = MutableSharedFlow<String>()
    val errorResponse = _errorResponse.asSharedFlow()

    fun createDeliveryNote(createDeliveryNotes: CreateDeliveryNoteRequest) {
        viewModelScope.launch {
            val createDeliveryNoteResponse = deliveryNotesRepository.createDeliveryNotes(createDeliveryNotes)
            when (createDeliveryNoteResponse) {
                is APIResource.Success -> {
                    _createDeliveryNotes.emit(createDeliveryNoteResponse.value)
                }
                is APIResource.Loading ->{

                }
                is APIResource.Error ->{
                    _errorResponse.emit(parseErrors(createDeliveryNoteResponse))
                }
            }
        }
    }
}