package com.ultratechies.ghala.data.models.responses.user

data class FetchAllUsersResponse(
    val assignedWarehouse: Int,
    val email: String,
    val firstName: String,
    val id: Int,
    val lastName: String,
    val password: String,
    val phoneNumber: String,
    val profilePhoto: List<Int>,
    val role: String
)