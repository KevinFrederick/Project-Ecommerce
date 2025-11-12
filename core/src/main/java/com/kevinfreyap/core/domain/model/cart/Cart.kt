package com.kevinfreyap.core.domain.model.cart

import com.kevinfreyap.core.domain.model.product.Product

data class Cart(
    val product: Product,
    val quantity: Int
)
