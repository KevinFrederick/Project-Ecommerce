package com.kevinfreyap.shared_wishlist.domain.model

data class WishlistItem(
    val productId: String = "",
    val dateAdded: Long = 0,
    val productCategory: String = "",
    val productName: String = "",
    val productPrice: Int = 0,
    val productImage: String = "",
    val isAvailable: Boolean = true
)
