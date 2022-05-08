package com.ultratechies.ghala.ui.warehouses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.responses.WarehousesResponse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.WarehouseRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class WarehousesViewModel @Inject constructor(
    private val repository: WarehouseRepository
) : ViewModel() {
    private val _warehouses: MutableLiveData<APIResource<WarehousesResponse>> = MutableLiveData()
    val warehouses: LiveData<APIResource<WarehousesResponse>>
        get() = _warehouses

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
     val errorMessage: LiveData<String> get() =  _errorMessage

    private val _deletedWarehouseResponse: MutableLiveData<APIResource<Unit>> = MutableLiveData()
    val deletedWarehouseResponse: LiveData<APIResource<Unit>>
        get() = _deletedWarehouseResponse

    fun getWarehouses() = viewModelScope.launch {
        _warehouses.value = APIResource.Loading
        _warehouses.value = repository.getWarehouses()
    }

    fun fetchWarehouses() {
        viewModelScope.launch {
            val warehouseResponse = repository.getWarehouses()
            when (warehouseResponse) {
                is APIResource.Success -> {
                    _warehouses.postValue(warehouseResponse)
                }
                is APIResource.Loading -> {

                }
                is APIResource.Error -> {
                    _errorMessage.postValue(parseErrors(warehouseResponse))
                }
            }
        }
    }

    fun deleteWarehouse(id: Int) = viewModelScope.launch {
        _deletedWarehouseResponse.value = APIResource.Loading
        _deletedWarehouseResponse.value = repository.deleteWarehouse(id)
    }
}