package com.ultratechies.ghala.di

import com.google.gson.Gson
import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.data.repository.*
import com.ultratechies.ghala.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        userApi: UserApi,
        userPrefs: AppDatasource,
        gson: Gson
    ): UserRepository = UserRepositoryImpl(userApi, userPrefs, gson)

/*    @Singleton
    @Provides
    fun providesOTPRepository( getOTPApi: GetOTPApi): GetOTPRepository = GetOTPRepositoryImpl(getOTPApi)*/

    @Singleton
    @Provides
    fun provideWarehouseRepo(warehouseAPI: WarehouseApi): WarehouseRepository =
        WarehouseRepositoryImpl(warehouseAPI)

    @Singleton
    @Provides
    fun providesOrders(ordersApi: OrdersApi, appDatasource: AppDatasource): OrdersRepository =
        OrdersRepositoryImpl(ordersApi, appDatasource = appDatasource)

    @Singleton
    @Provides
    fun providesInventoryRepo(
        inventoryApi: InventoryApi,
        appDatasource: AppDatasource
    ): InventoryRepository =
        InventoryRepositoryImpl(inventoryApi, appDatasource = appDatasource)

    @Singleton
    @Provides
    fun providesDeliveryNotes(deliveryNotesApi: DeliveryNotesApi,appDatasource: AppDatasource): DeliveryNotesRepository =
        DeliveryNotesRepositoryImpl(deliveryNotesApi,appDatasource = appDatasource)

}