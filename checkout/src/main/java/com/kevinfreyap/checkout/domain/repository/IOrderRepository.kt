package com.kevinfreyap.checkout.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_transaction.domain.model.PaymentMethod
import com.kevinfreyap.shared_cart.domain.model.Cart
import com.kevinfreyap.shared_transaction.domain.model.TransactionAddress
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt

interface IOrderRepository {
    suspend fun submitOrder(
        items: List<Cart>,
        address: TransactionAddress,
        payment: PaymentMethod,
        voucher: String?
    ): Resource<TransactionReceipt>
}