package com.ultratechies.ghala.data

import com.ultratechies.ghala.data.models.AppDatasource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AccessTokenInterceptor ( val appDatasource: AppDatasource ): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val value = runBlocking {

        }
        return chain.proceed( chain.request() )
    }

}