package com.ultratechies.ghala.data.models.responses.home

data class OrderValueResponse(
    var month: Double,
    val monthName: String,
    val sum: Int,
    val year: Double,
    val yearName: String
)