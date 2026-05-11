package com.kevinfreyap.shared_voucher.domain.usecase

import com.kevinfreyap.shared_voucher.domain.repository.IVoucherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewVoucherCountUseCase @Inject constructor(
    private val voucherRepository: IVoucherRepository
) {
    operator fun invoke(): Flow<Int> = voucherRepository.getNewVoucherCount()
}