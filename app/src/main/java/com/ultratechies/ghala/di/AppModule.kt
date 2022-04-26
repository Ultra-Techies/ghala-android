package com.ultratechies.ghala.di

import androidx.viewbinding.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ultratechies.ghala.data.repository.WarehouseApi
import com.ultratechies.ghala.data.repository.WarehouseRepository
import com.ultratechies.ghala.data.repository.WarehouseRepositoryImpl
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
    fun providesWarehouseAPI(retrofit: Retrofit): WarehouseApi =
        retrofit.create(WarehouseApi::class.java)

    @Singleton
    @Provides
    fun provideWarehouseRepo(warehouseAPI: WarehouseApi): WarehouseRepository = WarehouseRepositoryImpl(warehouseAPI)
}