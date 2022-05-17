package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.responses.warehouses.AddWarehouseResponse
import com.ultratechies.ghala.data.models.responses.warehouses.Warehouse
import com.ultratechies.ghala.data.models.responses.warehouses.WarehousesResponse
import javax.inject.Inject

interface WarehouseRepository {
    suspend fun getWarehouses(): APIResource<WarehousesResponse>
    suspend fun addNewWarehouse(warehouse: Warehouse): APIResource<AddWarehouseResponse>
    suspend fun deleteWarehouse(id: Int): APIResource<Unit>
    suspend fun editWarehouse(editWarehouseRequest: Warehouse): APIResource<Unit>
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

    override suspend fun deleteWarehouse(id: Int): APIResource<Unit> = safeApiCall {
        warehouseApi.deleteWarehouse(id)
    }

    override suspend fun editWarehouse(editWarehouseRequest: Warehouse): APIResource<Unit> = safeApiCall {
        warehouseApi.editWarehouse(editWarehouseRequest)
    }
}