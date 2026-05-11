package com.kevinfreyap.shared_cart.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_cart.domain.model.CartSummary
import com.kevinfreyap.shared_cart.domain.repository.ICartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartSummaryUseCase @Inject constructor(
    private val cartRepository: ICartRepository,
    private val calculateSummaryUseCase: CalculateSummaryUseCase
) {
    operator fun invoke(): Flow<CartSummary> {
        return cartRepository.getCartItems().map { resource ->
            if (resource is Resource.Success) {
                val items = resource.data.filter { cart ->
                    cart.isAvailable
                }

                calculateSummaryUseCase(items, null)
            } else {
                CartSummary(
                    subtotal = 0,
                    shippingFee = 0,
                    voucherDiscount = 0,
                    total = 0
                )
            }
        }
    }
}