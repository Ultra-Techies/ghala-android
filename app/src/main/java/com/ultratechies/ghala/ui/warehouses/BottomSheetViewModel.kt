package com.ultratechies.ghala.ui.warehouses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.responses.warehouses.AddWarehouseResponse
import com.ultratechies.ghala.data.models.responses.warehouses.Warehouse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.WarehouseRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetViewModel @Inject constructor(private val warehouseRepo: WarehouseRepository) : ViewModel() {

    private val _newWarehouseResponse = MutableSharedFlow<AddWarehouseResponse>()
    val newWarehouseResponse = _newWarehouseResponse.asSharedFlow()

    private val _editWarehouseResponse: MutableLiveData<APIResource<Any>> = MutableLiveData()
    val editWarehouseResponse: LiveData<APIResource<Any>>
        get() = _editWarehouseResponse

    private val _errorResponse = MutableSharedFlow<String>()
    val errorResponse = _errorResponse.asSharedFlow()

    private val _unAuthorizedError = MutableSharedFlow<Boolean>()
    val unAuthorizedError = _unAuthorizedError.asSharedFlow()

    fun newWarehouse(warehouse: Warehouse) {
        viewModelScope.launch {
            when (val warehouseResponse = warehouseRepo.addNewWarehouse(warehouse)) {
                is APIResource.Success -> {
                    _newWarehouseResponse.emit(warehouseResponse.value)
                }
                is APIResource.Error -> {
                    if (warehouseResponse.errorCode == 403)
                        _unAuthorizedError.emit(true)
                    else
                    _errorResponse.emit(parseErrors(warehouseResponse))
                }
                is APIResource.Loading ->{

                }
            }

        }
    }

    fun editWarehouse(editWarehouseRequest: Warehouse) = viewModelScope.launch {
        _editWarehouseResponse.value = APIResource.Loading
        _editWarehouseResponse.value = warehouseRepo.editWarehouse(editWarehouseRequest)
    }
}