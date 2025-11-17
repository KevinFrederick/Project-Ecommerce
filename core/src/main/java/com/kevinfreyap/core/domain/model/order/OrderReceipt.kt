package com.kevinfreyap.core.domain.model.order

import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.utils.PaymentMethod

data class OrderReceipt(
    val orderId: String,
    val datePlaced: Long,
    val totalPaid: Int,
    val subtotal: Int,
    val shippingFee: Int,
    val discountAmount: Int,
    val orderStatus: String,
    val shippingAddress: UserAddress,
    val itemsPurchased: List<OrderItem>,
    val paymentMethod: PaymentMethod
)
