package com.ultratechies.ghala.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.data.models.responses.orders.OrderResponseItem
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.OrdersRepository
import com.ultratechies.ghala.domain.models.UserModel
import com.ultratechies.ghala.utils.parseErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(val ordersRepo: OrdersRepository,val appDatasource: AppDatasource) : ViewModel() {
    private val _getOrders = MutableSharedFlow<List<OrderResponseItem>>()
    val getOrders = _getOrders.asSharedFlow()

    private val _errorResponse = MutableSharedFlow<String>()
    val errorResponse = _errorResponse.asSharedFlow()

    private val _user = MutableStateFlow(UserModel(
        assignedWarehouse = 0,
        email = "",
        firstName = "",
        id = 0,
        lastName = "",
        password = "",
        phoneNumber = "",
        profilePhoto = listOf(),
        role = ""
    ))

    init {
        viewModelScope.launch {
            appDatasource.getUserFromPreferencesStore().collectLatest { userModel->
                if (userModel != null) {
                    _user.value = userModel
                }
            }
        }
    }

    fun fetchOrders(){
        viewModelScope.launch {
            val orderResponse = ordersRepo.getOrders(_user.value)
            when(orderResponse){
                is APIResource.Success->{
                    _getOrders.emit(orderResponse.value.asReversed())
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