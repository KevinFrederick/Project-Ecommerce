package com.kevinfreyap.core.data.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.order.OrderItem
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.voucher.Voucher
import com.kevinfreyap.core.domain.repository.IOrderRepository
import com.kevinfreyap.core.domain.repository.IVoucherRepository
import com.kevinfreyap.core.domain.usecase.cart.CalculateSummaryService
import com.kevinfreyap.core.utils.PaymentMethod
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Mock Repository (Pretend API)
@Singleton
class OrderRepository @Inject constructor(
    // Shouldn't be depend on other repository or UseCase but easier for mock
    private val voucherRepository: IVoucherRepository,
    private val calculateSummaryService: CalculateSummaryService
): IOrderRepository{
    override suspend fun submitOrder(
        items: List<Cart>,
        address: UserAddress,
        payment: PaymentMethod,
        voucher: String?
    ): Resource<OrderReceipt> {
        delay(1500)

        // "Backend" looks up the voucher object from the database
        var voucherObj: Voucher? = null
        if (!voucher.isNullOrBlank()){
            val voucherResult = voucherRepository.getVoucherByCode(voucher)
            if (voucherResult is Resource.Success) {
                voucherObj = voucherResult.data
            } else {
                return Resource.Error("ERROR_VOUCHER_NOT_FOUND")
            }
        }

        // "Backend" recalculates totals using the standard formula
        // We reuse the UseCase here to ensure the math is identical to the UI.
        val summary = calculateSummaryService(items, voucherObj)

        val itemsPurchased = items.map { cart ->
            OrderItem(
                productId = cart.product.id,
                title = cart.product.title,
                quantity = cart.quantity,
                pricePerItem = cart.product.price,
                imageUrl = cart.product.images.firstOrNull() ?: ""
            )
        }

        val receipt = OrderReceipt(
            orderId = "ORD-${UUID.randomUUID().toString().substring(0,6).uppercase()}",
            datePlaced = System.currentTimeMillis(),
            totalPaid = summary.total,
            subtotal = summary.subtotal,
            shippingFee = summary.shippingFee,
            discountAmount = summary.voucherDiscount,
            orderStatus = "Processing",
            shippingAddress = address,
            itemsPurchased = itemsPurchased,
            paymentMethod = payment
        )

        // Assume Success
        return Resource.Success(receipt)
    }
}