package com.ultratechies.ghala.ui.warehouses

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.responses.AddWarehouseResponse
import com.ultratechies.ghala.data.models.responses.Warehouse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetViewModel @Inject constructor(private val warehouseRepo: WarehouseRepository) : ViewModel() {

    private val _newWarehouseResponse = MutableSharedFlow<AddWarehouseResponse>()
    val newWarehouseResponse = _newWarehouseResponse.asSharedFlow()

    private val _errorResponse = MutableSharedFlow<String>()
    val errorResponse = _errorResponse.asSharedFlow()

    fun newWarehouse(warehouse: Warehouse) {
        viewModelScope.launch {
            val warehouseResponse = warehouseRepo.addNewWarehouse(warehouse)
            when (warehouseResponse) {
                is APIResource.Success -> {
                    _newWarehouseResponse.emit(warehouseResponse.value)
                }
                is APIResource.Error -> {
                    _errorResponse.emit(warehouseResponse.errorCode.toString())
                }
            }

        }
    }

}