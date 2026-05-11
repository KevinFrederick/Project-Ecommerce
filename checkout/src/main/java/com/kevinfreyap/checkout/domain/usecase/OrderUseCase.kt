package com.kevinfreyap.checkout.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_transaction.domain.model.PaymentMethod
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import com.kevinfreyap.shared_user.domain.model.UserAddress

interface OrderUseCase {
    suspend fun placeOrder(
        address: UserAddress,
        paymentMethod: PaymentMethod,
        voucher: String?
    ): Resource<TransactionReceipt>
}