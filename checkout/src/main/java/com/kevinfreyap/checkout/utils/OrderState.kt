package com.kevinfreyap.checkout.utils

import com.kevinfreyap.core.domain.model.order.OrderReceipt

sealed interface OrderState {
    object Idle: OrderState
    object Loading: OrderState
    data class OrderSuccess (val receipt: OrderReceipt) : OrderState
}