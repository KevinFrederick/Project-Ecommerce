package com.kevinfreyap.shared_cart.data.source.remote.network

import com.kevinfreyap.shared_cart.domain.model.CartProduct

sealed interface AvailabilityStatus{
    data class Available(val product: CartProduct): AvailabilityStatus
    object Unavailable: AvailabilityStatus
    object Unknown: AvailabilityStatus
}