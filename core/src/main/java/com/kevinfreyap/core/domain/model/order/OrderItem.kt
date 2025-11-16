package com.kevinfreyap.core.domain.model.order

data class OrderItem(
    val productId: String,
    val title: String,
    val quantity: Int,
    val pricePerItem: Int
)
