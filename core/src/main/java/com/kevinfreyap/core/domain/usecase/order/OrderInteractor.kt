package com.kevinfreyap.core.domain.usecase.order

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.repository.CartRepository
import com.kevinfreyap.core.data.repository.OrderRepository
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.utils.PaymentMethod
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OrderInteractor @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
): OrderUseCase {
    override suspend fun placeOrder(
        address: UserAddress,
        paymentMethod: PaymentMethod,
        voucher: String?
    ): Resource<OrderReceipt> {
        val cartItems = cartRepository.getCartItems().first().data
        if (cartItems.isNullOrEmpty()) {
            return Resource.Error("ERROR_EMPTY_CART")
        }

        val result = orderRepository.submitOrder(
            items = cartItems,
            address = address,
            payment = paymentMethod,
            voucher = voucher
        )

        if (result is Resource.Success) {
            cartRepository.clearCart()
        }

        return result
    }
}