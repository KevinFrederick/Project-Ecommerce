package com.kevinfreyap.core.domain.model.product

data class Product(
    val id: Int,
    val title: String,
    val category: ProductCategory,
    val description: String,
    val price: Int,
    val images: List<String>,
    val slug: String,
    val creationAt: String,
    val updatedAt: String
)
