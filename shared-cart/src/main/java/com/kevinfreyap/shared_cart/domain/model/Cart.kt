package com.kevinfreyap.shared_cart.domain.model

data class Cart(
    val product: CartProduct,
    val quantity: Int,
    val isAvailable: Boolean = true
)
