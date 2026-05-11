package com.kevinfreyap.shared_cart.domain.model

data class CartSummary(
    val subtotal: Int,
    val shippingFee: Int,
    val voucherDiscount: Int,
    val total: Int
)
