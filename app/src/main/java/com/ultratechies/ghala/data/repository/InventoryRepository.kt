package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.InventoryResponseItem
import javax.inject.Inject

interface InventoryRepository {
    suspend fun getInventory(): APIResource<List<InventoryResponseItem>>
}

class InventoryRepositoryImpl @Inject constructor(private val inventoryApi: InventoryApi) :
    InventoryRepository, BaseRepo() {
    override suspend fun getInventory() = safeApiCall {
        inventoryApi.getInventory()
    }

}