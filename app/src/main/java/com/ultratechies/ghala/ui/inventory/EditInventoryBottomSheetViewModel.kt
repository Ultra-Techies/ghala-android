package com.ultratechies.ghala.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.requests.inventory.EditInventoryRequest
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.InventoryRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditInventoryBottomSheetViewModel @Inject constructor(val inventoryRepository: InventoryRepository) : ViewModel() {

    private val _editInventory = MutableSharedFlow<Any>()
    val editInventory = _editInventory.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage =_errorMessage.asSharedFlow()

    private val _unAuthorizedError = MutableSharedFlow<Boolean>()
    val unAuthorizedError = _unAuthorizedError.asSharedFlow()

    fun editInventoryItem(editInventoryRequest: EditInventoryRequest){
        viewModelScope.launch {
            when(  val response = inventoryRepository.editInventoryItem(editInventoryRequest)){
                is APIResource.Success ->{
                    _editInventory.emit(response.value)
                }
                is APIResource.Error ->{
                    if (response.errorCode == 403)
                        _unAuthorizedError.emit(true)
                    else
                        _errorMessage.emit(parseErrors(response))
                }
                is APIResource.Loading ->{

                }
            }
        }
    }


}