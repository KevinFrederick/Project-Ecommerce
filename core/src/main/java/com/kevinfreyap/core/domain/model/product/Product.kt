package com.kevinfreyap.core.domain.model.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    val title: String,
    val category: ProductCategory,
    val description: String,
    val price: Int,
    val images: List<String>,
    val slug: String,
    val creationAt: String,
    val updatedAt: String
): Parcelable
