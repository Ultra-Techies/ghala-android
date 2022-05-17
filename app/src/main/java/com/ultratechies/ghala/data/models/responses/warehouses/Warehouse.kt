package com.ultratechies.ghala.data.models.responses.warehouses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Warehouse (
    val id: Int,
    val name: String,
    val location: String
): Parcelable