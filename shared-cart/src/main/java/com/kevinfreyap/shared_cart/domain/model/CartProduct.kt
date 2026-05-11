package com.kevinfreyap.shared_cart.domain.model

data class CartProduct(
    val id: String,
    val title: String,
    val price: Int,
    val images: List<String>,
    val creationAt: Long,
    val updatedAt: Long
)
