package com.kevinfreyap.core.domain.model.wishlist

data class FirestoreWishlistItem(
    val productId: String = "",
    val dateAdded: Long = 0,
    val productName: String = "",
    val productPrice: Int = 0,
    val productImage: String = "",
    val productCategory: String = ""
)
