package com.ultratechies.ghala.data.models.responses

data class InventoryResponseItem(
    val category: String,
    val image: Any,
    val name: String,
    val ppu: Int,
    val quantity: Int,
    val sku: Int,
    val status: String,
    val warehouseId: Int
)