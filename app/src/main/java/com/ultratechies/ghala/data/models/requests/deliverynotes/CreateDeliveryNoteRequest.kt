package com.ultratechies.ghala.data.models.requests.deliverynotes

data class CreateDeliveryNoteRequest(
    val deliverWindow: String? = null,
    val orderIds: List<Int>? = null,
    val route: String? = null,
    val warehouseId: Int? = null
)