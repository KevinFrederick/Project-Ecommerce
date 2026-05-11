package com.kevinfreyap.checkout.data.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_transaction.domain.model.PaymentMethod
import com.kevinfreyap.checkout.domain.repository.IOrderRepository
import com.kevinfreyap.shared_cart.domain.model.AppliedDiscount
import com.kevinfreyap.shared_cart.domain.model.Cart
import com.kevinfreyap.shared_cart.domain.usecase.CalculateSummaryUseCase
import com.kevinfreyap.shared_transaction.domain.model.TransactionAddress
import com.kevinfreyap.shared_transaction.domain.model.TransactionItem
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import com.kevinfreyap.shared_transaction.domain.model.TransactionStatus
import com.kevinfreyap.shared_voucher.domain.repository.IVoucherRepository
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Mock Repository (Pretend API)
@Singleton
class OrderRepository @Inject constructor(
    // Shouldn't be depended on other repository or UseCase but easier for mock
    private val voucherRepository: IVoucherRepository,
    private val calculateSummary: CalculateSummaryUseCase
): IOrderRepository {
    override suspend fun submitOrder(
        items: List<Cart>,
        address: TransactionAddress,
        payment: PaymentMethod,
        voucher: String?
    ): Resource<TransactionReceipt> {
        delay(1500)

        // "Backend" looks up the voucher object from the database
        var discountObj: AppliedDiscount? = null
        if (!voucher.isNullOrBlank()){
            val voucherResult = voucherRepository.getVoucherByCode(voucher)
            if (voucherResult is Resource.Success) {
                discountObj = AppliedDiscount(
                    minSpend = voucherResult.data.minSpend,
                    isPercentage = voucherResult.data.isPercentage,
                    amount = voucherResult.data.discountAmount
                )
            } else {
                return Resource.Error("ERROR_VOUCHER_NOT_FOUND")
            }
        }

        // "Backend" recalculates totals using the standard formula
        // We reuse the UseCase here to ensure the math is identical to the UI.
        val summary = calculateSummary(items, discountObj)

        val itemsPurchased = items.map { cart ->
            TransactionItem(
                productId = cart.product.id,
                title = cart.product.title,
                quantity = cart.quantity,
                pricePerItem = cart.product.price,
                imageUrl = cart.product.images.firstOrNull() ?: ""
            )
        }

        val receipt = TransactionReceipt(
            orderId = "ORD-${UUID.randomUUID().toString().substring(0,6).uppercase()}",
            datePlaced = System.currentTimeMillis(),
            totalPaid = summary.total,
            subtotal = summary.subtotal,
            shippingFee = summary.shippingFee,
            discountAmount = summary.voucherDiscount,
            transactionStatus = TransactionStatus.PROCESSING,
            shippingAddress = address,
            itemsPurchased = itemsPurchased,
            paymentMethod = payment
        )

        // Assume Success
        return Resource.Success(receipt)
    }
}