package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.orders.OrderResponseItem
import retrofit2.http.GET
import retrofit2.http.Path

interface OrdersApi {

 /*   @GET("api/order/all")
    suspend fun getOrders():List<OrderResponseItem>*/

    @GET("api/order/wh/{id}")
    suspend fun  getOrdersByWarehouseId(
        @Path("id") warehouseId: Int?
    ): List<OrderResponseItem>

}