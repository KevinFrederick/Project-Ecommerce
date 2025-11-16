package com.kevinfreyap.core.data.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.order.OrderItem
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.repository.IOrderRepository
import com.kevinfreyap.core.utils.PaymentMethod
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(): IOrderRepository{
    override suspend fun submitOrder(
        items: List<Cart>,
        address: UserAddress,
        payment: PaymentMethod,
        voucher: String?
    ): Resource<OrderReceipt> {
        delay(1500)

        if (voucher == "FAIL") {
            return Resource.Error("INVALID_VOUCHER")
        }

        val itemsPurchased = items.map { cart ->
            OrderItem(
                productId = cart.product.id.toString(),
                title = cart.product.title,
                quantity = cart.quantity,
                pricePerItem = cart.product.price
            )
        }

        val subtotal = items.sumOf { it.product.price.toDouble() * it.quantity }
        val shippingFee = if (0 < subtotal && subtotal < 100) 20.0 else 0.0
        val voucherDisc = 0
        val total = (subtotal + shippingFee - voucherDisc).coerceAtLeast(0.0)

        val receipt = OrderReceipt(
            orderId = "ORD-${UUID.randomUUID().toString().substring(0,6).uppercase()}",
            datePlaced = System.currentTimeMillis(),
            totalPaid = total.toInt(),
            orderStatus = "Processing",
            shippingAddress = address,
            itemsPurchased = itemsPurchased
        )

        return Resource.Success(receipt)
    }
}