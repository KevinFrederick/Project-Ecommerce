package com.kevinfreyap.shared_transaction.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kevinfreyap.shared_transaction.domain.model.PaymentMethod
import com.kevinfreyap.shared_transaction.domain.model.TransactionStatus

@Entity(tableName = "transaction_history")
data class TransactionEntity(
    @PrimaryKey
    val transactionId: String,
    val datePlaced: Long,
    val totalPaid: Int,
    val subtotal: Int,
    val shippingFee: Int,
    val discountAmount: Int,
    val transactionStatus: TransactionStatus,
    val shippingAddressJson: String,
    val itemsPurchasedJson: String,
    val paymentMethod: PaymentMethod
)