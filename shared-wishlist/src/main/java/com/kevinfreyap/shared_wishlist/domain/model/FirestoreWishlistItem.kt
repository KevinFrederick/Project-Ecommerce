package com.kevinfreyap.shared_wishlist.domain.model

data class FirestoreWishlistItem(
    val productId: String = "",
    val dateAdded: Long = 0,
    val productName: String = "",
    val productPrice: Int = 0,
    val productImage: String = "",
    val productCategory: String = ""
)
