package com.kevinfreyap.core.domain.usecase.order

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.auth.FirebaseAuth
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.repository.ICartRepository
import com.kevinfreyap.core.domain.repository.IOrderRepository
import com.kevinfreyap.core.domain.repository.ITransactionRepository
import com.kevinfreyap.core.domain.repository.IVoucherRepository
import com.kevinfreyap.core.utils.PaymentMethod
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OrderInteractor @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val cartRepository: ICartRepository,
    private val voucherRepository: IVoucherRepository,
    private val transactionRepository: ITransactionRepository,
    private val connectivityManager: ConnectivityManager,
    private val firebaseAuth: FirebaseAuth
): OrderUseCase {
    override suspend fun placeOrder(
        address: UserAddress,
        paymentMethod: PaymentMethod,
        voucher: String?
    ): Resource<OrderReceipt> {
        if (!isInternetAvailable()) {
            return Resource.Error("ERROR_NO_CONNECTION")
        }

        if (firebaseAuth.currentUser == null) {
            return Resource.Error("ERROR_USER_NOT_FOUND")
        }

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

        when(result) {
            is Resource.Loading -> {
                return Resource.Error("An unexpected error occurred.")
            }
            is Resource.Success -> {
                val receipt = result.data
                transactionRepository.saveOrder(receipt)
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

    private fun isInternetAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}