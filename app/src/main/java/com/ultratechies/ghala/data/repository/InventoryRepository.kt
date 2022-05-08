package com.ultratechies.ghala.data.repository

import android.util.Log
import com.ultratechies.ghala.data.models.requests.inventory.AddInventoryRequest
import com.ultratechies.ghala.data.models.requests.inventory.EditInventoryRequest
import com.ultratechies.ghala.data.models.responses.inventory.AddNewInventoryResponse
import com.ultratechies.ghala.data.models.responses.inventory.InventoryResponseItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

interface InventoryRepository {
    suspend fun getInventory(): APIResource<List<InventoryResponseItem>>
    suspend fun addInventoryItem(addInventoryRequest: AddInventoryRequest): APIResource<AddNewInventoryResponse>
    suspend fun editInventoryItem(editInventoryRequest: EditInventoryRequest): APIResource<String>
    suspend fun deleteInventoryItem(sku: Int): APIResource<String>
}

class InventoryRepositoryImpl @Inject constructor(
    private val inventoryApi: InventoryApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    InventoryRepository, BaseRepo() {

    override suspend fun getInventory() = safeApiCall {
        inventoryApi.getInventory()
    }

    override suspend fun addInventoryItem(addInventoryRequest: AddInventoryRequest) = safeApiCall {
        inventoryApi.addInventoryItem(addInventoryRequest)
    }

    override suspend fun editInventoryItem(editInventoryRequest: EditInventoryRequest): APIResource<String> {
        return withContext(dispatcher) {
            try {
                inventoryApi.editInventoryItem(editInventoryRequest)
                APIResource.Success("Success")
            } catch (throwable: Throwable) {
                Log.e("--->", throwable.message.toString())
                when (throwable) {
                    is HttpException -> {
                        APIResource.Error(
                            false,
                            throwable.code(),
                            throwable.response()?.errorBody()
                        )
                    }
                    else -> {
                        APIResource.Error(true, null, null)
                    }
                }
            }

        }

    }

    override suspend fun deleteInventoryItem(sku: Int): APIResource<String> {
        return withContext(dispatcher) {
            try {
                inventoryApi.deleteInventoryItem(sku)
                APIResource.Success("Success")
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        APIResource.Error(
                            false,
                            throwable.code(),
                            throwable.response()?.errorBody()
                        )
                    }
                    else -> {
                        APIResource.Error(true, null, null)
                    }
                }
            }
        }
    }
}