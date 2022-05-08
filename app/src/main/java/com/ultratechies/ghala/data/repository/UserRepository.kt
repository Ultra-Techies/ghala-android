package com.ultratechies.ghala.data.repository

import com.google.gson.Gson
import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.data.models.requests.auth.CheckUserExistsRequest
import com.ultratechies.ghala.data.models.requests.auth.GetOTPRequest
import com.ultratechies.ghala.data.models.requests.user.CreateUserRequest
import com.ultratechies.ghala.data.models.requests.user.UpdateUserRequest
import com.ultratechies.ghala.data.models.requests.user.VerifyUserRequest
import com.ultratechies.ghala.domain.models.UserModel
import com.ultratechies.ghala.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userPrefs: AppDatasource,
    val gson: Gson,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository, BaseRepo() {

    override suspend fun getUserExists(checkUserExistsRequest: CheckUserExistsRequest) =
        safeApiCall {
            userApi.getIfUserExists(checkUserExistsRequest)
        }

    override suspend fun getOTP(otpRequest: GetOTPRequest) = safeApiCall {
        userApi.getOTP(otpRequest)
    }

    override suspend fun createUser(createUserRequest: CreateUserRequest) = safeApiCall {
        val userId = userApi.createUser(createUserRequest)
        val response = getUserById(userId.id)
        if (response is APIResource.Success) {
            return@safeApiCall response.value
        } else {
            throw Exception("Unable to create user")
        }
    }

    override suspend fun getUserById(id: Int) = safeApiCall {
        val response = userApi.getUserById(id)
        withContext(dispatcher) {
            userPrefs.saveUserToPreferencesStore(response)
        }
        return@safeApiCall response
    }

    override suspend fun verifyUser(verifyUserRequest: VerifyUserRequest) = safeApiCall {
        val response = userApi.verifyUser(verifyUserRequest)
        if (response.has("verified")) {
            return@safeApiCall false
        } else {
            // convert json to user model
            val userModel = gson.fromJson(response, UserModel::class.java)
            // store it
            userPrefs.saveUserToPreferencesStore(userModel)
            // return true
            return@safeApiCall true
        }
    }

    override suspend fun updateUser(updateUserRequest: UpdateUserRequest): APIResource<String> {
        return withContext(dispatcher) {
            try {
                userApi.updateUser(updateUserRequest)
                APIResource.Success("success")
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        APIResource.Error(
                            false,
                            throwable.code(),
                            throwable.response()?.errorBody()
                        )
                    }
                    else -> {
                        APIResource.Error(true, null, null)
                    }
                }
            }
        }
    }


}