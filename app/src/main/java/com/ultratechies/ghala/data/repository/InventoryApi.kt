package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.requests.inventory.AddInventoryRequest
import com.ultratechies.ghala.data.models.requests.inventory.EditInventoryRequest
import com.ultratechies.ghala.data.models.responses.inventory.AddNewInventoryResponse
import com.ultratechies.ghala.data.models.responses.inventory.InventoryResponseItem
import retrofit2.http.*

interface InventoryApi {

    @GET("allinventory")
    suspend fun getInventory(): List<InventoryResponseItem>

    @POST("inventory")
    suspend fun addInventoryItem(
        @Body addInventoryItem: AddInventoryRequest
    ): AddNewInventoryResponse

    @PUT("inventory")
    suspend fun editInventoryItem(
        @Body editInventoryRequest: EditInventoryRequest
    )

    @DELETE("inventory/{sku}")
    suspend fun deleteInventoryItem(
        @Path("sku") sku: Int
    )

}