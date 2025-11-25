package com.kevinfreyap.core.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voucher")
data class VoucherEntity(
    @PrimaryKey
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val discountAmount: Double,
    val isPercentage: Boolean,
    val minSpend: Double,
    val expiryDate: Long,
    val type: String,
    val isUsed: Boolean,
    val isNew: Boolean
)
