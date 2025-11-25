package com.kevinfreyap.core.domain.usecase.voucher

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.voucher.Voucher
import kotlinx.coroutines.flow.Flow

interface VoucherUseCase {
    fun getUserVouchers(): Flow<Resource<List<Voucher>>>
    fun getNewVoucherCount(): Flow<Int>
    fun listenToPublicVouchers()
    suspend fun applyVoucher(voucherCode: String, cartTotal: Double): Resource<Voucher>
    suspend fun syncVouchers()
    suspend fun markAllAsSeen()
    suspend fun clearVouchers()
}