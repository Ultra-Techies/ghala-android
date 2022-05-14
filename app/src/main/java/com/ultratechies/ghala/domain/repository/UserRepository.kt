package com.ultratechies.ghala.domain.repository

import com.ultratechies.ghala.data.models.requests.auth.CheckUserExistsRequest
import com.ultratechies.ghala.data.models.requests.auth.FetchUserByPhoneNumber
import com.ultratechies.ghala.data.models.requests.auth.GetOTPRequest
import com.ultratechies.ghala.data.models.requests.user.CreateUserRequest
import com.ultratechies.ghala.data.models.requests.user.UpdateUserRequest
import com.ultratechies.ghala.data.models.requests.user.VerifyUserRequest
import com.ultratechies.ghala.data.models.responses.auth.CheckUserExistsResponse
import com.ultratechies.ghala.data.models.responses.auth.GetOTPResponse
import com.ultratechies.ghala.data.repository.APIResource
import com.ultratechies.ghala.domain.models.UserModel

interface UserRepository {
    suspend fun getUserExists(checkUserExistsRequest: CheckUserExistsRequest): APIResource<CheckUserExistsResponse>
    suspend fun getOTP(otpRequest: GetOTPRequest): APIResource<GetOTPResponse>
    suspend fun createUser(createUserRequest: CreateUserRequest): APIResource<Boolean>
    suspend fun getUserById(): APIResource<UserModel>
    suspend fun verifyUser(verifyUserRequest: VerifyUserRequest): APIResource<Boolean>
    suspend fun fetchUserByPhoneNumber(fetchUserByPhoneNumber: FetchUserByPhoneNumber): APIResource<UserModel>
    suspend fun updateUser(updateUserRequest: UpdateUserRequest): APIResource<String>
}