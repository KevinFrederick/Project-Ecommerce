package com.kevinfreyap.shared_voucher.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_voucher.domain.model.Voucher
import kotlinx.coroutines.flow.Flow

interface IVoucherRepository {
    fun getUserVouchers(): Flow<Resource<List<Voucher>>>
    suspend fun getVoucherByCode(code: String): Resource<Voucher>
    fun getNewVoucherCount(): Flow<Int>
    fun getPublicVouchersStream(): Flow<List<Voucher>>
    suspend fun checkNewVoucherInBackground(): List<Voucher>
    suspend fun markVoucherAsUsed(voucher: Voucher)
    suspend fun syncVouchers()
    suspend fun markAllAsSeen()
    suspend fun clearVouchers()
    suspend fun processAndSaveVouchers(incomingVouchers: List<Voucher>): List<Voucher>
}