package com.kevinfreyap.core.domain.model.cart

data class CartSummary(
    val subtotal: Int,
    val shippingFee: Int,
    val voucherDiscount: Int,
    val total: Int
)
