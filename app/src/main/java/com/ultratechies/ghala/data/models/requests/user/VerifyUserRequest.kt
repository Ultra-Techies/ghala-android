package com.ultratechies.ghala.data.models.requests.user

data class VerifyUserRequest(
    val password: String,
    val phoneNumber: String
)