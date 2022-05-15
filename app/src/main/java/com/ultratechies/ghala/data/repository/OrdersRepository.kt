package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.data.models.responses.orders.OrderResponseItem
import com.ultratechies.ghala.domain.models.UserModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface OrdersRepository {
    suspend fun getOrders(value: UserModel): APIResource<List<OrderResponseItem>>

}

class OrdersRepositoryImpl @Inject constructor(
    private val ordersApi: OrdersApi,
    val appDatasource: AppDatasource
) : OrdersRepository,
    BaseRepo() {

    override suspend fun getOrders(value: UserModel) = safeApiCall {
        val user = appDatasource.getUserFromPreferencesStore().first()
        ordersApi.getOrdersByWarehouseId(user?.assignedWarehouse)
    }


}