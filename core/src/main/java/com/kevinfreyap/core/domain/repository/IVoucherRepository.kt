package com.kevinfreyap.core.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.voucher.Voucher
import kotlinx.coroutines.flow.Flow

interface IVoucherRepository {
    fun getUserVouchers(): Flow<Resource<List<Voucher>>>
    suspend fun getVoucherByCode(code: String): Resource<Voucher>
    fun getNewVoucherCount(): Flow<Int>
    fun listenToPublicVouchers()
    suspend fun markVoucherAsUsed(voucher: Voucher)
    suspend fun syncVouchers()
    suspend fun markAllAsSeen()
    suspend fun clearVouchers()
}