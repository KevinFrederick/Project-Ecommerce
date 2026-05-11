package com.kevinfreyap.shared_voucher.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_voucher.domain.model.Voucher
import com.kevinfreyap.shared_voucher.domain.repository.IVoucherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserVoucherUseCase @Inject constructor(
    private val voucherRepository: IVoucherRepository
) {
    operator fun invoke(): Flow<Resource<List<Voucher>>> = voucherRepository.getUserVouchers()
}