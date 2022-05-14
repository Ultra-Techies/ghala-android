package com.ultratechies.ghala.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ultratechies.ghala.BuildConfig
import com.ultratechies.ghala.data.AccessTokenInterceptor
import com.ultratechies.ghala.data.models.AppDatasource
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
object NetworkModule {
    @Provides
    @Singleton
    fun providesOkhttp(appDatasource: AppDatasource): OkHttpClient {
        val okhhtp = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.setLevel(HttpLoggingInterceptor.Level.BODY)
            okhhtp.addInterceptor(logger)
        }
        // add interceptor
        okhhtp.addInterceptor(AccessTokenInterceptor(appDatasource))
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
    fun providesWarehouseAPI(retrofit: Retrofit): WarehouseApi =
        retrofit.create(WarehouseApi::class.java)

    @Singleton
    @Provides
    fun providesUserService(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Singleton
    @Provides
    fun orderService(retrofit: Retrofit): OrdersApi = retrofit.create(OrdersApi::class.java)

    @Singleton
    @Provides
    fun inventoryService(retrofit: Retrofit): InventoryApi =
        retrofit.create(InventoryApi::class.java)

    @Singleton
    @Provides
    fun deliveryNotesService(retrofit: Retrofit): DeliveryNotesApi = retrofit.create(
        DeliveryNotesApi::class.java
    )
}