package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.orders.OrderResponseItem
import javax.inject.Inject

interface OrdersRepository {
    suspend fun getOrders(): APIResource<List<OrderResponseItem>>

}

class OrdersRepositoryImpl @Inject constructor(val ordersApi: OrdersApi) : OrdersRepository,
    BaseRepo() {
    override suspend fun getOrders() = safeApiCall {
        ordersApi.getOrders()
    }

}