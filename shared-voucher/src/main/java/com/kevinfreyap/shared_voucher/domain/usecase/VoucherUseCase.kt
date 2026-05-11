package com.kevinfreyap.shared_voucher.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_voucher.domain.model.Voucher

interface VoucherUseCase {
    suspend fun applyVoucher(voucherCode: String, cartTotal: Double): Resource<Voucher>
    suspend fun syncVouchers()
    suspend fun clearVouchers()
}