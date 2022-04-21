package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.AddWarehouseResponse
import com.ultratechies.ghala.data.models.responses.Warehouse
import com.ultratechies.ghala.data.models.responses.WarehousesResponse
import javax.inject.Inject

interface WarehouseRepository {
    suspend fun getWarehouses(): APIResource<WarehousesResponse>
    suspend fun addNewWarehouse(warehouse: Warehouse): APIResource<AddWarehouseResponse>
}

class WarehouseRepositoryImpl @Inject constructor(
    private val warehouseApi: WarehouseApi
) : WarehouseRepository, BaseRepo() {


    override suspend fun getWarehouses() = safeApiCall {
        warehouseApi.getWarehouses()
    }

    override suspend fun addNewWarehouse(warehouse: Warehouse) = safeApiCall {
        warehouseApi.addNewWarehouse(warehouse)
    }
}