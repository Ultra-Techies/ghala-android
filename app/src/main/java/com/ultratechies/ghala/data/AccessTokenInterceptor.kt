package com.ultratechies.ghala.data

import com.ultratechies.ghala.data.models.AppDatasource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.locks.ReentrantLock

class AccessTokenInterceptor(val appDatasource: AppDatasource) : Interceptor {
    val lock = ReentrantLock()

    override fun intercept(chain: Interceptor.Chain): Response {
        // exclude endpoints
        if (chain.request().url.encodedPath.contains("api/users/exists")
            || (chain.request().url.encodedPath.contains("/login"))
        ) {
            runBlocking {
                appDatasource.clear()
            }
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
            val res = chain.proceed(request)
//            if (res.code == 403 || res.code == 401) {
//                synchronized(this) {
//                    lock.lock()
//
//                    res.request.newBuilder().delete()
//                    runBlocking { appDatasource.clear() }
//
//                    val intent = Intent(App.currentActivity()?.applicationContext, AuthActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    App.currentActivity()?.applicationContext?.startActivity(intent)
//
//                    lock.unlock()
//                }
//            }
            return res
        } else {
            chain.proceed(chain.request())
        }
    }

}