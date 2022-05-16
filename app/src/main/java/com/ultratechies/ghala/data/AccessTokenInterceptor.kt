package com.ultratechies.ghala.data

import android.content.Context
import android.content.Intent
import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.ui.auth.AuthActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.locks.ReentrantLock

class AccessTokenInterceptor(val appDatasource: AppDatasource, val context: Context) : Interceptor {
    private val lock = ReentrantLock()

    override fun intercept(chain: Interceptor.Chain): Response {
        // exclude endpoints
        if (chain.request().url.encodedPath.contains("api/users/exists")
            || (chain.request().url.encodedPath.contains("/login")) || (chain.request().url.encodedPath.contains(
                "/api/otp"
            ))
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
            if (res.code == 403) {
                synchronized(this) {
                    lock.lock()

                    res.request.newBuilder().delete()
                    runBlocking { appDatasource.clear() }

                    val intent = Intent(context, AuthActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)

                    lock.unlock()
                }
            }
            return res
        } else {
            chain.proceed(chain.request())
        }
    }

}