package com.kevinfreyap.checkout.utils

import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt

sealed interface OrderState {
    object Idle: OrderState
    object Loading: OrderState
    data class OrderSuccess (val receipt: TransactionReceipt) : OrderState
}