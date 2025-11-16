package com.kevinfreyap.core.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.utils.PaymentMethod

interface IOrderRepository {
    suspend fun submitOrder(
        items: List<Cart>,
        address: UserAddress,
        payment: PaymentMethod,
        voucher: String?
    ): Resource<OrderReceipt>
}