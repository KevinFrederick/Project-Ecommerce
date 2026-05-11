package com.kevinfreyap.shared_voucher.data.event

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.shared_voucher.domain.repository.IVoucherRepository
import javax.inject.Inject

class VoucherAuthListener @Inject constructor(
    private val voucherRepository: IVoucherRepository,
): IAuthEvenListener {
    override suspend fun onUserLoggedIn() {
        voucherRepository.syncVouchers()
    }

    override suspend fun onUserLoggedOut() {
        runCatching { voucherRepository.clearVouchers() }
    }
}