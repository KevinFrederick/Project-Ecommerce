package com.kevinfreyap.shared_transaction.domain.model

data class TransactionReceipt(
    val orderId: String = "",
    val datePlaced: Long = 0,
    val totalPaid: Int = 0,
    val subtotal: Int = 0,
    val shippingFee: Int = 0,
    val discountAmount: Int = 0,
    val transactionStatus: TransactionStatus = TransactionStatus.PROCESSING,
    val shippingAddress: TransactionAddress = TransactionAddress(),
    val itemsPurchased: List<TransactionItem> = emptyList(),
    val paymentMethod: PaymentMethod = PaymentMethod.CASH
)
