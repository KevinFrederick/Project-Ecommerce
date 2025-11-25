package com.kevinfreyap.core.domain.usecase.cart

import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.cart.CartSummary
import com.kevinfreyap.core.domain.model.voucher.Voucher
import javax.inject.Inject

class CalculateSummaryService @Inject constructor() {
    operator fun invoke(cartItems: List<Cart>, voucher: Voucher?): CartSummary {
        val validItems = cartItems.filter { it.isAvailable }

        val subtotal = validItems.sumOf { it.product.price * it.quantity }.toDouble()
        val shippingFee = if (subtotal > 0 && subtotal < 100) 20.0 else 0.0

        var voucherDisc = 0.0
        if (voucher != null && voucher.isActive()) {
            if (subtotal >= voucher.minSpend) {
                voucherDisc = if (voucher.isPercentage) {
                    subtotal * (voucher.discountAmount / 100)
                } else {
                    voucher.discountAmount
                }
            }
        }

        if (voucherDisc > subtotal) voucherDisc = subtotal
        val total = (subtotal + shippingFee - voucherDisc).coerceAtLeast(0.0)

        return CartSummary(
            subtotal = subtotal.toInt(),
            shippingFee = shippingFee.toInt(),
            voucherDiscount = voucherDisc.toInt(),
            total = total.toInt()
        )
    }
}