package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.WarehousesResponse
import javax.inject.Inject

interface WarehouseRepository {
    suspend fun getWarehouses(): APIResource<WarehousesResponse>
}

class WarehouseRepositoryImpl @Inject constructor(
    private val warehouseApi: WarehouseApi
) : WarehouseRepository, BaseRepo() {


    override suspend fun getWarehouses() = safeApiCall {
        warehouseApi.getWarehouses()
    }
}