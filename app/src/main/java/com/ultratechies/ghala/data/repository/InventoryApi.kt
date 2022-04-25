package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.InventoryResponseItem
import retrofit2.http.GET

interface InventoryApi {

    @GET("allinventory")
    suspend fun getInventory(): List<InventoryResponseItem>

}