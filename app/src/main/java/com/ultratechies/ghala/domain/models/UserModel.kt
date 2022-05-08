package com.ultratechies.ghala.domain.models

data class UserModel(
    val assignedWarehouse: Int,
    val email: String,
    val firstName: String,
    val id: Int,
    val lastName: String,
    val password: String,
    val phoneNumber: String,
    val profilePhoto: List<Any>,
    val role: String
)