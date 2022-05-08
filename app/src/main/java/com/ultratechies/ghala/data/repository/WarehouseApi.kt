package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.AddWarehouseResponse
import com.ultratechies.ghala.data.models.responses.Warehouse
import com.ultratechies.ghala.data.models.responses.WarehousesResponse
import retrofit2.Response
import retrofit2.http.*

interface WarehouseApi {

    @HTTP(method = "GET", path = "warehouses", hasBody = false)
    suspend fun getWarehouses(
    ) : WarehousesResponse

    @POST("warehouse")
    suspend fun addNewWarehouse(
        @Body warehouse: Warehouse
    ): AddWarehouseResponse

    @DELETE("warehouse/{id}")
    suspend fun deleteWarehouse(@Path("id") id: Int): Response<Unit>

    @PUT("warehouse")
    suspend fun editWarehouse(
        @Body editWarehouseRequest: Warehouse
    ): Response<Unit>
}