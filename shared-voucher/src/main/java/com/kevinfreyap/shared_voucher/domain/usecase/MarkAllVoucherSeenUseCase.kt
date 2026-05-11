package com.kevinfreyap.shared_voucher.domain.usecase

import com.kevinfreyap.shared_voucher.domain.repository.IVoucherRepository
import javax.inject.Inject

class MarkAllVoucherSeenUseCase @Inject constructor(
    private val voucherRepository: IVoucherRepository
) {
    suspend operator fun invoke() = voucherRepository.markAllAsSeen()
}