package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.requests.auth.CheckUserExistsRequest
import com.ultratechies.ghala.data.models.requests.auth.FetchUserByPhoneNumber
import com.ultratechies.ghala.data.models.requests.auth.GetOTPRequest
import com.ultratechies.ghala.data.models.requests.user.CreateUserRequest
import com.ultratechies.ghala.data.models.requests.user.UpdateUserRequest
import com.ultratechies.ghala.data.models.requests.user.VerifyUserRequest
import com.ultratechies.ghala.data.models.responses.auth.CheckUserExistsResponse
import com.ultratechies.ghala.data.models.responses.auth.GetOTPResponse
import com.ultratechies.ghala.data.models.responses.auth.LoginResponse
import com.ultratechies.ghala.data.models.responses.user.CreateUserResponse
import com.ultratechies.ghala.domain.models.UserModel
import retrofit2.http.*

interface UserApi {

    @POST("api/users/exists")
    suspend fun getIfUserExists(
        @Body userExistsRequest: CheckUserExistsRequest
    ): CheckUserExistsResponse

    @POST("api/otp")
    suspend fun getOTP(
        @Body otpRequest: GetOTPRequest
    ): GetOTPResponse

    @POST("api/users")
    suspend fun createUser(
        @Body createUseRequest: CreateUserRequest
    ): CreateUserResponse

    @GET("api/users/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): UserModel

    /*  @PUT("login")
      suspend fun verifyUser(
          @Body verifyUserRequest: VerifyUserRequest
      ): JsonObject*/

    @POST("login")
    suspend fun verifyUser(
        @Body verifyUserRequest: VerifyUserRequest
    ): LoginResponse


    @PUT("api/users")
    suspend fun updateUser(
        @Body updateUserRequest: UpdateUserRequest
    )

    @PUT("api/users/fetch")
    suspend fun fetchUser(
        @Body fetchUserByPhoneNumber: FetchUserByPhoneNumber
    ): UserModel


}