package com.kevinfreyap.shared_transaction.domain.model

enum class TransactionStatus(val displayName: String) {
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): TransactionStatus {
            return try {
                valueOf(value.uppercase())
            } catch (_: Exception) {
                PROCESSING
            }
        }
    }
}