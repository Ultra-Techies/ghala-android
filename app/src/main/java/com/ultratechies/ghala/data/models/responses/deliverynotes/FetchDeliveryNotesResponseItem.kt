package com.ultratechies.ghala.data.models.responses.deliverynotes

data class FetchDeliveryNotesResponseItem(
    val createdTime: String,
    val deliveryWindow: String,
    val id: Int,
    val noteCode: String,
    val orders: List<Order>,
    val route: String,
    val status: String,
    val warehouseId: Int
)

data class Order(
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