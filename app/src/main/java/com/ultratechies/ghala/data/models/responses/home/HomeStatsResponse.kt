package com.ultratechies.ghala.data.models.responses.home

data class HomeStatsResponse(
    val inventoryValue: Int,
    val orderValue: List<OrderValueResponse>
)