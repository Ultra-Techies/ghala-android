package com.ultratechies.ghala.data.models.requests.user

data class CreateUserRequest(
    val assignedWarehouse: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val phoneNumber: String?,
    val profilePhoto: List<Int>
)