package com.kevinfreyap.core.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kevinfreyap.core.utils.PaymentMethod

@Entity(tableName = "transaction_history")
data class TransactionEntity(
    @PrimaryKey
    val transactionId: String,
    val datePlaced: Long,
    val totalPaid: Int,
    val subtotal: Int,
    val shippingFee: Int,
    val discountAmount: Int,
    val orderStatus: String,
    val shippingAddressJson: String,
    val itemsPurchasedJson: String,
    val paymentMethod: PaymentMethod
)
