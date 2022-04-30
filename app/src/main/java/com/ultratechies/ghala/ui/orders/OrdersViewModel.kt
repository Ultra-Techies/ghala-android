package com.ultratechies.ghala.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.responses.orders.OrderResponseItem
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.OrdersRepository
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(val ordersRepo: OrdersRepository) : ViewModel() {
    private val _getOrders = MutableSharedFlow<List<OrderResponseItem>>()
    val getOrders = _getOrders.asSharedFlow()

    private val _errorResponse = MutableSharedFlow<String>()
    val errorResponse = _errorResponse.asSharedFlow()

    fun fetchOrders(){
        viewModelScope.launch {
            val orderResponse = ordersRepo.getOrders()
            when(orderResponse){
                is APIResource.Success->{
                    _getOrders.emit(orderResponse.value)
                }
                is APIResource.Loading ->{

                }
                is APIResource.Error ->{
                    _errorResponse.emit(parseErrors(orderResponse))
                }
            }
        }
    }


}