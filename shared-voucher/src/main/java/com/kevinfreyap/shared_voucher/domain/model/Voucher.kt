package com.kevinfreyap.shared_voucher.domain.model

data class Voucher(
    val id: String = "",
    val code: String = "", // e.g. "SAVE50"
    val title: String = "", // "50% OFF"
    val description: String = "",
    val discountAmount: Double = 0.0, // 0.5 or 50.0
    val isPercentage: Boolean = true, // true if %, false if fixed $
    val minSpend: Double = 0.0,
    val expiryDate: Long = 0L,
    val type: String = "PRIVATE",
    val isUsed: Boolean = false,
    val isNew: Boolean = false
) {
    fun isActive(): Boolean {
        return !isUsed && expiryDate > System.currentTimeMillis()
    }
}