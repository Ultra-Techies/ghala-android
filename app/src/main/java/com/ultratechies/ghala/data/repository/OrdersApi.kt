package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.OrderResponseItem
import retrofit2.http.GET

interface OrdersApi {

    @GET("orders")
    suspend fun getOrders():List<OrderResponseItem>

}