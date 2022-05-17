package com.ultratechies.ghala.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.requests.inventory.AddInventoryRequest
import com.ultratechies.ghala.data.models.responses.inventory.AddNewInventoryResponse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.InventoryRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddInventoryBottomSheetViewModel @Inject constructor(val inventoryRepository: InventoryRepository) :
    ViewModel() {

    private val _addInventoryItem = MutableSharedFlow<AddNewInventoryResponse>()
    val addInventoryItem = _addInventoryItem.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _unAuthorizedError = MutableSharedFlow<Boolean>()
    val unAuthorizedError = _unAuthorizedError.asSharedFlow()

    fun addInventory(addInventoryRequest: AddInventoryRequest) {
        viewModelScope.launch {
            when ( val addInventoryResponse = inventoryRepository.addInventoryItem(addInventoryRequest)) {
                is APIResource.Success -> {
                    _addInventoryItem.emit(addInventoryResponse.value)
                }
                is APIResource.Loading ->{

                }
                is APIResource.Error ->{
                    if (addInventoryResponse.errorCode == 403)
                        _unAuthorizedError.emit(true)
                    else
                        _errorMessage.emit(parseErrors(addInventoryResponse))
                }
            }
        }
    }

}