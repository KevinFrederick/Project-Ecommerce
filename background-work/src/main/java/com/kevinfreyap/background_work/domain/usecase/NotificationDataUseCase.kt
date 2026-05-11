package com.kevinfreyap.background_work.domain.usecase

import android.util.Log
import com.kevinfreyap.core.domain.notification.INotificationService
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import com.kevinfreyap.shared_transaction.domain.repository.ITransactionRepository
import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import com.kevinfreyap.shared_voucher.domain.repository.IVoucherRepository
import com.kevinfreyap.shared_wishlist.domain.repository.IWishlistRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NotificationDataUseCase @Inject constructor(
    private val authRepository: IAuthenticationRepository,
    private val transactionRepository: ITransactionRepository,
    private val wishlistRepository: IWishlistRepository,
    private val voucherRepository: IVoucherRepository,
    private val userRepository: IUserRepository,
    private val notificationService: INotificationService
) {
    suspend operator fun invoke() = coroutineScope {

        if (!authRepository.isUserLoggedIn()) {
            Log.d("NotificationWorker", "User logged out. Stop background work.")
            return@coroutineScope
        }

        val updatedOrderDeferred = async { transactionRepository.simulateOrderStatusUpdates() }
        val backInStockProductDeferred = async { wishlistRepository.validateWishlistAvailability() }
        val newVouchersDeferred = async { voucherRepository.checkNewVoucherInBackground() }

        // Wait for all data to finish syncing
        val updatedOrders = updatedOrderDeferred.await()
        val unavailableWishlists = backInStockProductDeferred.await()
        val newVouchers = newVouchersDeferred.await()

        val notifStatus = userRepository.getNotificationSettings().first()

        if (notifStatus.system) {
            if (updatedOrders.isNotEmpty()) {
                updatedOrders.forEach { product ->
                    val title = "Status Updated"
                    val message = "Order #${product.orderId} is now ${product.transactionStatus.displayName}!"
                    val type = "TRANSACTION"
                    notificationService.showNotification(title, message, type)
                }
            }

            if (unavailableWishlists.isNotEmpty()) {
                unavailableWishlists.forEach { product ->
                    val title = "Back in Stock!"
                    val message = "${product.productName} is available now!"
                    val type = "WISHLIST"
                    notificationService.showNotification(title, message, type)
                }
            }
        }

        if (notifStatus.promotions) {
            if (newVouchers.isNotEmpty()) {
                newVouchers.forEach { voucher ->
                    val discountText = if (voucher.isPercentage) {
                        "${voucher.discountAmount.toInt()}%"
                    } else {
                        "$${voucher.discountAmount}"
                    }

                    val title = "New Voucher! \uD83C\uDF81"
                    val message = "Get $discountText off with code: ${voucher.code}"
                    val type = "VOUCHER"
                    notificationService.showNotification(title, message, type)
                }
            }
        }
    }
}