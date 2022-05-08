package com.ultratechies.ghala.data.repository

import com.google.gson.JsonObject
import com.ultratechies.ghala.data.models.requests.auth.CheckUserExistsRequest
import com.ultratechies.ghala.data.models.requests.auth.GetOTPRequest
import com.ultratechies.ghala.data.models.requests.user.CreateUserRequest
import com.ultratechies.ghala.data.models.requests.user.UpdateUserRequest
import com.ultratechies.ghala.data.models.requests.user.VerifyUserRequest
import com.ultratechies.ghala.data.models.responses.auth.CheckUserExistsResponse
import com.ultratechies.ghala.data.models.responses.auth.GetOTPResponse
import com.ultratechies.ghala.data.models.responses.user.CreateUserResponse
import com.ultratechies.ghala.domain.models.UserModel
import retrofit2.http.*

interface UserApi {

    @POST("users")
    suspend fun getIfUserExists(
        @Body userExistsRequest: CheckUserExistsRequest
    ): CheckUserExistsResponse

    @POST("otp")
    suspend fun getOTP(
        @Body otpRequest: GetOTPRequest
    ): GetOTPResponse

    @POST("user")
    suspend fun createUser(
        @Body createUseRequest: CreateUserRequest
    ): CreateUserResponse

    @GET("user/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): UserModel

    @PUT("users")
    suspend fun verifyUser(
        @Body verifyUserRequest: VerifyUserRequest
    ): JsonObject

    @PUT("user")
    suspend fun updateUser(
        @Body updateUserRequest: UpdateUserRequest
    )


}