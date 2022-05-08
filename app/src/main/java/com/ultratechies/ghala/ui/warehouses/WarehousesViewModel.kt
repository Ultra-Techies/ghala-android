package com.ultratechies.ghala.ui.warehouses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.responses.WarehousesResponse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WarehousesViewModel @Inject constructor(
    private val repository: WarehouseRepository
): ViewModel() {
    private val _warehouses: MutableLiveData<APIResource<WarehousesResponse>> = MutableLiveData()
    val warehouses: LiveData<APIResource<WarehousesResponse>>
        get() = _warehouses

    private val _deletedWarehouseResponse: MutableLiveData<APIResource<Any>> = MutableLiveData()
    val deletedWarehouseResponse: LiveData<APIResource<Any>>
        get() = _deletedWarehouseResponse

    fun getWarehouses() = viewModelScope.launch {
        _warehouses.value = APIResource.Loading
        _warehouses.value = repository.getWarehouses()
    }

    fun deleteWarehouse(id: Int) = viewModelScope.launch {
        _deletedWarehouseResponse.value = APIResource.Loading
        _deletedWarehouseResponse.value = repository.deleteWarehouse(id)
    }
}