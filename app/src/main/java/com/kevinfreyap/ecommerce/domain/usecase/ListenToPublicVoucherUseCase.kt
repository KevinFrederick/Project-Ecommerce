package com.kevinfreyap.ecommerce.domain.usecase

import com.kevinfreyap.core.di.ApplicationScope
import com.kevinfreyap.core.domain.notification.INotificationService
import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import com.kevinfreyap.shared_voucher.domain.repository.IVoucherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListenToPublicVoucherUseCase @Inject constructor(
    private val voucherRepository: IVoucherRepository,
    private val userRepository: IUserRepository,
    private val notificationService: INotificationService,
    @param:ApplicationScope private val externalScope: CoroutineScope,
) {
    operator fun invoke() {
        externalScope.launch {
            voucherRepository.getPublicVouchersStream().collect { incomingVouchers ->
                val newVouchers = voucherRepository.processAndSaveVouchers(incomingVouchers)

                val settingStatus = userRepository.getNotificationSettings().first()

                if (settingStatus.promotions && newVouchers.isNotEmpty()) {
                    newVouchers.forEach { voucher ->
                        val discountText = if (voucher.isPercentage) "${voucher.discountAmount}%" else "$${voucher.discountAmount}"

                        notificationService.showNotification(
                            title = "New Voucher!",
                            message = "Get $discountText off with code: ${voucher.code}",
                            type = "VOUCHER"
                        )
                    }
                }
            }
        }
    }
}