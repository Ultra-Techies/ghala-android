package com.ultratechies.ghala.data

import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.data.repository.BaseRepo
import com.ultratechies.ghala.data.repository.UserApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject


class TokenAuthenticator : Authenticator, BaseRepo() {
    @Inject
    lateinit var appDatasource: AppDatasource

    @Inject
    lateinit var userApi: UserApi

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val refreshToken = appDatasource.getRefreshToken().first()
            val tokenResponse = safeApiCall { userApi.refreshToken("`Bearer $refreshToken") }
            if (tokenResponse is APIResource.Success) {
                appDatasource.saveRefreshToken(tokenResponse.value.refreshToken)
                appDatasource.saveAccessToken(tokenResponse.value.accessToken)

                val newBuilder = response.request.newBuilder()
                    .addHeader("Authorization", "Bearer ${tokenResponse.value.accessToken}")
                return@runBlocking newBuilder.build()
            }
            null
        }
    }
}