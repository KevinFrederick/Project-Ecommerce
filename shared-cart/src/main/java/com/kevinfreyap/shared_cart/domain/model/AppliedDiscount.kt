package com.kevinfreyap.shared_cart.domain.model

data class AppliedDiscount (
    val minSpend: Double,
    val isPercentage: Boolean,
    val amount: Double
)