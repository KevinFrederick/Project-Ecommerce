package com.kevinfreyap.core.domain.usecase.order

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.utils.PaymentMethod

interface OrderUseCase {
    suspend fun placeOrder(
        address: UserAddress,
        paymentMethod: PaymentMethod,
        voucher: String?
    ): Resource<OrderReceipt>
}