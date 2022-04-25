package com.ultratechies.ghala.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ultratechies.ghala.BuildConfig
import com.ultratechies.ghala.data.repository.*
import com.ultratechies.ghala.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesOkhttp(): OkHttpClient {
        val okhhtp = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.setLevel(HttpLoggingInterceptor.Level.BODY)

            okhhtp.addInterceptor(logger)
        }
        return okhhtp.build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit
        .Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .build()

    @Singleton
    @Provides
    fun apiService(retrofit: Retrofit): WarehouseApi =
        retrofit.create(WarehouseApi::class.java)

    @Singleton
    @Provides
    fun orderService(retrofit: Retrofit) :OrdersApi = retrofit.create(OrdersApi::class.java)

    @Singleton
    @Provides
    fun inventoryService(retrofit: Retrofit): InventoryApi = retrofit.create(InventoryApi::class.java)

    @Singleton
    @Provides
    fun provideWarehouseRepo(warehouseAPI: WarehouseApi): WarehouseRepository = WarehouseRepositoryImpl(warehouseAPI)

    @Singleton
    @Provides
    fun providesOrders(ordersApi: OrdersApi): OrdersRepository = OrdersRepositoryImpl(ordersApi)

    @Singleton
    @Provides
    fun providesInventoryRepo(inventoryApi: InventoryApi): InventoryRepository = InventoryRepositoryImpl(inventoryApi)
}