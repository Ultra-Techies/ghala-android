package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.orders.OrderResponseItem
import com.ultratechies.ghala.domain.models.UserModel
import javax.inject.Inject

interface OrdersRepository {
    suspend fun getOrders(value: UserModel): APIResource<List<OrderResponseItem>>

}

class OrdersRepositoryImpl @Inject constructor(private val ordersApi: OrdersApi) : OrdersRepository,
    BaseRepo() {

    override suspend fun getOrders(value: UserModel) = safeApiCall {
        if(value.role == "BASIC"){
            ordersApi.getOrdersByWarehouseId(value.assignedWarehouse)
        }else{
            ordersApi.getOrders()
        }

    }


}