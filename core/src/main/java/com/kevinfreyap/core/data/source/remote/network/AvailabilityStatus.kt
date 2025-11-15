package com.kevinfreyap.core.data.source.remote.network

import com.kevinfreyap.core.domain.model.product.Product

sealed interface AvailabilityStatus{
    data class Available(val product: Product): AvailabilityStatus
    object Unavailable: AvailabilityStatus
    object Unknown: AvailabilityStatus
}