package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.requests.inventory.AddInventoryRequest
import com.ultratechies.ghala.data.models.requests.inventory.EditInventoryRequest
import com.ultratechies.ghala.data.models.responses.inventory.AddNewInventoryResponse
import com.ultratechies.ghala.data.models.responses.inventory.InventoryResponseItem
import retrofit2.http.*

interface InventoryApi {

    @GET("api/inventory/all")
    suspend fun getInventory(): List<InventoryResponseItem>

    @GET("api/inventory/wh/{id}")
    suspend fun getInventoryByWarehouseId(@Path("id") sku: Int?
    ): List<InventoryResponseItem>

    @POST("api/inventory")
    suspend fun addInventoryItem(
        @Body addInventoryItem: AddInventoryRequest
    ): AddNewInventoryResponse

    @PUT("api/inventory")
    suspend fun editInventoryItem(
        @Body editInventoryRequest: EditInventoryRequest
    )

    @DELETE("api/inventory/{sku}")
    suspend fun deleteInventoryItem(
        @Path("sku") sku: Int
    )

}