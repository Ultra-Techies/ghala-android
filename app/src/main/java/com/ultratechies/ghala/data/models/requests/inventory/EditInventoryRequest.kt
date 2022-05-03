package com.ultratechies.ghala.data.models.requests.inventory

data class EditInventoryRequest(
    val category: String,
    val name: String,
    val ppu: String,
    val quantity: String,
    val sku: Int,
    val status: String,
    val warehouseId: String
)