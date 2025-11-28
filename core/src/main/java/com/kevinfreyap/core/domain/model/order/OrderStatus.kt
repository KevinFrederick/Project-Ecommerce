package com.kevinfreyap.core.domain.model.order

enum class OrderStatus(val displayName: String) {
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): OrderStatus {
            return try {
                valueOf(value.uppercase())
            } catch (_: Exception) {
                PROCESSING
            }
        }
    }
}