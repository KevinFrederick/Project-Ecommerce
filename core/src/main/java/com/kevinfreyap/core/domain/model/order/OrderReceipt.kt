package com.kevinfreyap.core.domain.model.order

import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.utils.PaymentMethod

data class OrderReceipt(
    val orderId: String = "",
    val datePlaced: Long = 0,
    val totalPaid: Int = 0,
    val subtotal: Int = 0,
    val shippingFee: Int = 0,
    val discountAmount: Int = 0,
    val orderStatus: String = "",
    val shippingAddress: UserAddress = UserAddress(),
    val itemsPurchased: List<OrderItem> = emptyList(),
    val paymentMethod: PaymentMethod = PaymentMethod.CASH
)
