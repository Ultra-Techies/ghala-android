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

    fun editInventoryItem(editInventoryRequest: EditInventoryRequest){
        viewModelScope.launch {
            val response = inventoryRepository.editInventoryItem(editInventoryRequest)
            when(response){
                is APIResource.Success ->{
                    _editInventory.emit(response.value)
                }
                is APIResource.Error ->{
                    _errorMessage.emit(parseErrors(response))
                }
                else ->{

                }
            }
        }
    }


}