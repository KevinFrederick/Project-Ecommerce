package com.kevinfreyap.product.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductUi(
    val id: String,
    val name: String,
    val price: Int,
    val imageUrls: List<String>
): Parcelable
