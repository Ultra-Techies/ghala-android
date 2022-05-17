package com.ultratechies.ghala.data.models.responses.inventory

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class InventoryResponseItem(
    val category: String,
    val image:@RawValue Any,
    val name: String,
    val ppu: Int,
    val quantity: Int,
    val sku: Int,
    val status: String,
    val skuCode: String,
    val warehouseId: Int
):Parcelable