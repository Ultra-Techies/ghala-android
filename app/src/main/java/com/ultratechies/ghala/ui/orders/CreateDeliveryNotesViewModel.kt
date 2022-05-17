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
class CreateDeliveryNotesViewModel @Inject constructor(private val deliveryNotesRepository: DeliveryNotesRepository) :ViewModel(){

    private val _createDeliveryNotes= MutableSharedFlow<CreateDeliveryNoteResponse>()
    val createDeliveryNotes = _createDeliveryNotes.asSharedFlow()

    private val _errorResponse = MutableSharedFlow<String>()
    val errorResponse = _errorResponse.asSharedFlow()

    private val _unAuthorizedError = MutableSharedFlow<Boolean>()
    val unAuthorizedError = _unAuthorizedError.asSharedFlow()

    fun createDeliveryNote(createDeliveryNotes: CreateDeliveryNoteRequest) {
        viewModelScope.launch {
            when (val createDeliveryNoteResponse = deliveryNotesRepository.createDeliveryNotes(createDeliveryNotes)) {
                is APIResource.Success -> {
                    _createDeliveryNotes.emit(createDeliveryNoteResponse.value)
                }
                is APIResource.Loading ->{

                }
                is APIResource.Error ->{
                    if (createDeliveryNoteResponse.errorCode == 403)
                        _unAuthorizedError.emit(true)
                    else
                        _errorResponse.emit(parseErrors(createDeliveryNoteResponse))
                }
            }
        }
    }
}