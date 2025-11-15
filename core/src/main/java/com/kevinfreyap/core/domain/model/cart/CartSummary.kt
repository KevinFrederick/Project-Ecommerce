package com.kevinfreyap.core.domain.model.cart

data class CartSummary(
    val subtotal: Int,
    val shippingFee: Int,
    val total: Int
)
