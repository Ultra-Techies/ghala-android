package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.orders.OrderResponseItem
import retrofit2.http.GET
import retrofit2.http.Path

interface OrdersApi {

    @GET("orders")
    suspend fun getOrders():List<OrderResponseItem>

    @GET("warehouseorders/{id}")
    suspend fun  getOrdersByWarehouseId(
        @Path("id") warehouseId: Int
    ): List<OrderResponseItem>

}