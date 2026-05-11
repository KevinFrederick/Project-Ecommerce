package com.kevinfreyap.shared_wishlist.domain.model

data class WishlistProduct(
    val id: String,
    val title: String,
    val price: Int,
    val category: ProductCategory,
    val images: List<String>,
    val creationAt: Long,
    val updatedAt: Long
)
