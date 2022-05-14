package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.AddWarehouseResponse
import com.ultratechies.ghala.data.models.responses.Warehouse
import com.ultratechies.ghala.data.models.responses.WarehousesResponse
import retrofit2.Response
import retrofit2.http.*

interface WarehouseApi {

    @HTTP(method = "GET", path = "api/warehouse/all", hasBody = false)
    suspend fun getWarehouses(
    ) : WarehousesResponse

    @POST("api/warehouse")
    suspend fun addNewWarehouse(
        @Body warehouse: Warehouse
    ): AddWarehouseResponse

    @DELETE("api/warehouse/{id}")
    suspend fun deleteWarehouse(@Path("id") id: Int): Response<Unit>

    @PUT("api/warehouse")
    suspend fun editWarehouse(
        @Body editWarehouseRequest: Warehouse
    ): Response<Unit>
}