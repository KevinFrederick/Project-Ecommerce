package com.kevinfreyap.shared_product.domain.model

data class Product(
    val id: String,
    val title: String,
    val category: ProductCategory,
    val description: String,
    val price: Int,
    val images: List<String>,
    val slug: String,
    val creationAt: Long,
    val updatedAt: Long
)
