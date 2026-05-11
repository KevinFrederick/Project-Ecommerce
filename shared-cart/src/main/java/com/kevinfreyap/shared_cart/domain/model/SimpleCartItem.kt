package com.kevinfreyap.shared_cart.domain.model

data class SimpleCartItem(
    val productId: Int = 0,

    val quantity: Int = 0,

    val title: String? = null,

    val price: Int? = null,

    val imageUrl: String? = null,

    val dateAdded: Long? = null
)
