package com.kevinfreyap.checkout.domain.usecase

import com.kevinfreyap.core.domain.network.INetworkMonitor
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_transaction.domain.model.PaymentMethod
import com.kevinfreyap.checkout.domain.repository.IOrderRepository
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import com.kevinfreyap.shared_cart.domain.repository.ICartRepository
import com.kevinfreyap.shared_transaction.domain.model.TransactionAddress
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import com.kevinfreyap.shared_transaction.domain.usecase.SaveTransactionUseCase
import com.kevinfreyap.shared_user.domain.model.UserAddress
import com.kevinfreyap.shared_voucher.domain.repository.IVoucherRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OrderInteractor @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val cartRepository: ICartRepository,
    private val voucherRepository: IVoucherRepository,
    private val authRepository: IAuthenticationRepository,
    private val saveTransaction: SaveTransactionUseCase,
    private val networkMonitor: INetworkMonitor
): OrderUseCase {
    override suspend fun placeOrder(
        address: UserAddress,
        paymentMethod: PaymentMethod,
        voucher: String?
    ): Resource<TransactionReceipt> {
        if (!networkMonitor.isInternetAvailable()) {
            return Resource.Error("ERROR_NO_CONNECTION")
        }

        if (!authRepository.isUserLoggedIn()) {
            return Resource.Error("ERROR_USER_NOT_FOUND")
        }

        val cartItems = cartRepository.getCartItems().first().data
        if (cartItems.isNullOrEmpty()) {
            return Resource.Error("ERROR_EMPTY_CART")
        }

        val transactionAddress = TransactionAddress(
            street = address.street,
            city = address.city,
            state = address.state,
            country = address.country,
            zipCode = address.zipCode
        )

        val result = orderRepository.submitOrder(
            items = cartItems,
            address = transactionAddress,
            payment = paymentMethod,
            voucher = voucher
        )

        when(result) {
            is Resource.Loading -> {
                return Resource.Error("An unexpected error occurred.")
            }
            is Resource.Success -> {
                val receipt = result.data
                saveTransaction(receipt)
                cartRepository.clearCart()
                cartRepository.clearFirestoreCart()

                if (!voucher.isNullOrBlank()) {
                    markVoucherAsUsed(voucher)
                }

                return result
            }
            is Resource.Error -> {
                return result
            }
        }
    }

    private suspend fun markVoucherAsUsed(code: String) {
        val resource = voucherRepository.getVoucherByCode(code)

        if (resource is Resource.Success) {
            val voucher = resource.data

            val usedVoucher = voucher.copy(
                isUsed = true,
                isNew = false
            )
            voucherRepository.markVoucherAsUsed(usedVoucher)
        }
    }
}