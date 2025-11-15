package com.kevinfreyap.cart.utils

sealed interface CheckoutActionState {
    object Idle: CheckoutActionState
    object Loading: CheckoutActionState
    object Navigate: CheckoutActionState
}