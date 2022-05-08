package com.ultratechies.ghala.data.models.requests.user

data class UpdateUserRequest(
    val assignedWarehouse: Int? = null,
    val email: String? = null,
    val firstName: String? = null,
    val id: Int? = null,
    val lastName: String? = null,
    val password: String? = null,
    val phoneNumber: String? = null,
    val profilePhoto: List<Int>? = null,
    val role: String? = null
)