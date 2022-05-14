package com.ultratechies.ghala.data

import com.ultratechies.ghala.data.models.AppDatasource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AccessTokenInterceptor(val appDatasource: AppDatasource) : Interceptor {

    //TODO: exempt urls that don't need token, redirect user to login if resp is 403/401
    // TODO: call refresh token
    override fun intercept(chain: Interceptor.Chain): Response {
        // exclude endpoints
        if (chain.request().url.encodedPath.contains("api/users/exists")) {
            appDatasource.clear()
            return chain.proceed(chain.request())
        }

        val token = runBlocking {
            try {
                appDatasource.getAccessToken().first()
            } catch (e: Exception) {
                null
            }
        }
        return if (token != null) {
            val request = chain.request().newBuilder().apply {
                addHeader("Authorization", "Bearer $token")
            }.build()
            chain.proceed(request)
        } else {
            chain.proceed(chain.request())
        }
    }

}