package com.ultratechies.ghala.data.models.responses

data class OrderResponseItem(
    val created: String,
    val customerId: String,
    val deliveryWindow: String,
    val due: String,
    val id: Int,
    val items: List<Item>,
    val route: String,
    val status: String,
    val value: Int,
    val orderCode: String,
    val warehouseId: Int
)
data class Item(
    val name: String,
    val ppu: Int,
    val quantity: Int,
    val sku: Int,
    val totalPrice: Int
)