package com.kevinfreyap.shared_transaction.domain.model

data class TransactionItem(
    val productId: String = "",
    val title: String = "",
    val quantity: Int = 0,
    val pricePerItem: Int = 0,
    val imageUrl: String = "",
)
