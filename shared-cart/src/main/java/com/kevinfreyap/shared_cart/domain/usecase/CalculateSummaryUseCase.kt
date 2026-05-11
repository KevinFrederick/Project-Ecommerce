package com.kevinfreyap.shared_cart.domain.usecase

import com.kevinfreyap.shared_cart.domain.model.AppliedDiscount
import com.kevinfreyap.shared_cart.domain.model.Cart
import com.kevinfreyap.shared_cart.domain.model.CartSummary
import javax.inject.Inject

class CalculateSummaryUseCase @Inject constructor() {
    operator fun invoke(cartItems: List<Cart>, discount: AppliedDiscount?): CartSummary {
        val validItems = cartItems.filter { it.isAvailable }

        val subtotal = validItems.sumOf { it.product.price * it.quantity }.toDouble()
        val shippingFee = if (subtotal > 0 && subtotal < 100) 20.0 else 0.0

        var discountTotal = 0.0
        if (discount != null && subtotal >= discount.minSpend) {
            discountTotal = if (discount.isPercentage) {
                subtotal * (discount.amount / 100)
            } else {
                discount.amount
            }
        }

        if (discountTotal > subtotal) discountTotal = subtotal
        val total = (subtotal + shippingFee - discountTotal).coerceAtLeast(0.0)

        return CartSummary(
            subtotal = subtotal.toInt(),
            shippingFee = shippingFee.toInt(),
            voucherDiscount = discountTotal.toInt(),
            total = total.toInt()
        )
    }
}