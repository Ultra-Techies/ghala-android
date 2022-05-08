package com.ultratechies.ghala.data.models.requests.inventory

data class AddInventoryRequest(
    val category: String,
    val name: String,
    val ppu: String,
    val quantity: String,
    val status: String,
    val warehouseId: String
)