package com.ultratechies.ghala.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepo {

    suspend fun <T : Any> safeApiCall(
        apiCall: suspend () -> T,
    ) : APIResource<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall.invoke()
                Log.d("BaseRepo", "safeApiCall: $response")
                APIResource.Success(response)
            } catch (throwable: Throwable) {
                when(throwable){
                    is HttpException -> {
                        Log.e("BaseRepo", "safeApiCall: ${throwable.message}")
                        APIResource.Error(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    else -> {
                        Log.e("BaseRepo", "safeApiCall: ${throwable.message}")
                        APIResource.Error(true, null, null)
                    }
                }
            }
        }
    }
}