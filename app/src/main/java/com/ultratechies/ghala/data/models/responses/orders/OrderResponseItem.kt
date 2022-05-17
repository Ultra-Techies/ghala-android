package com.ultratechies.ghala.data.models.responses.orders

data class OrderResponseItem(
    val createdDate: String,
    val createdTime: String,
    val customerName: String,
    val deliveryWindow: String,
    val due: String,
    val id: Int,
    val items: List<Item>,
    val orderCode: String,
    val route: String,
    val status: String,
    val value: Int,
    val warehouseId: Int
)
data class Item(
    val name: String,
    val ppu: Int,
    val quantity: Int,
    val sku: Int,
    val totalPrice: Int
)