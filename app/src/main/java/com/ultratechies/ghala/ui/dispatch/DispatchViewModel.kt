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
class DispatchViewModel @Inject constructor(private val deliveryNotesRepository: DeliveryNotesRepository) :
    ViewModel() {

    private val _fetchDeliveryNotes = MutableSharedFlow<List<FetchDeliveryNotesResponseItem>>()
    val fetchDeliveryNotes = _fetchDeliveryNotes.asSharedFlow()

    private val _changeDeliveryNoteStatus = MutableSharedFlow<FetchDeliveryNotesResponseItem>()
    val changeDeliveryNoteStatus = _changeDeliveryNoteStatus.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _unAuthorizedError = MutableSharedFlow<Boolean>()
    val unAuthorizedError = _unAuthorizedError.asSharedFlow()

    fun getFetchDeliveryNotes() {
        viewModelScope.launch {
            when (val fetchDeliveryNotesResponse = deliveryNotesRepository.fetchDeliveryNotes()) {
                is APIResource.Success -> {
                    _fetchDeliveryNotes.emit(fetchDeliveryNotesResponse.value.asReversed())
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    if (fetchDeliveryNotesResponse.errorCode == 403)
                        _unAuthorizedError.emit(true)
                    else
                        _errorMessage.emit(parseErrors(fetchDeliveryNotesResponse))
                }
            }
        }
    }

    fun changeDeliveryNoteStatus(id: Int, state: Int) {
        viewModelScope.launch {
            when (val changeNoteStatus =
                deliveryNotesRepository.changeDeliveryNoteStatus(id, state)) {
                is APIResource.Success -> {
                    getFetchDeliveryNotes()
                    _changeDeliveryNoteStatus.emit(changeNoteStatus.value)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    if (changeNoteStatus.errorCode == 403)
                        _unAuthorizedError.emit(true)
                    else
                        _errorMessage.emit(parseErrors(changeNoteStatus))
                }
            }
        }
    }

}