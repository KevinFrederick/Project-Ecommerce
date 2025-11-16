package com.kevinfreyap.core.domain.model.order

import com.kevinfreyap.core.domain.model.user.UserAddress

data class OrderReceipt(
    val orderId: String,
    val datePlaced: Long,
    val totalPaid: Int,
    val orderStatus: String,
    val shippingAddress: UserAddress,
    val itemsPurchased: List<OrderItem>
)
