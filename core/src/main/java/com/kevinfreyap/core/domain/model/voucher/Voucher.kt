package com.kevinfreyap.core.domain.model.voucher

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Voucher(
    @DocumentId
    val id: String = "",
    val code: String = "", // e.g. "SAVE50"
    val title: String = "", // "50% OFF"
    val description: String = "",
    val discountAmount: Double = 0.0, // 0.5 or 50.0
    @get:PropertyName("isPercentage")
    val isPercentage: Boolean = true, // true if %, false if fixed $
    val minSpend: Double = 0.0,
    val expiryDate: Long = 0L,
    val type: String = "PRIVATE",
    @get:PropertyName("isUsed")
    val isUsed: Boolean = false,
    @get:PropertyName("isNew")
    val isNew: Boolean = false
) {
    fun isActive(): Boolean {
        return !isUsed && expiryDate > System.currentTimeMillis()
    }
}
