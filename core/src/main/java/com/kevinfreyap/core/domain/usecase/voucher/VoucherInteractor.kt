package com.kevinfreyap.core.domain.usecase.voucher

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.di.ApplicationScope
import com.kevinfreyap.core.domain.model.voucher.Voucher
import com.kevinfreyap.core.domain.repository.IVoucherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VoucherInteractor @Inject constructor(
    private val voucherRepository: IVoucherRepository,
    @param:ApplicationScope private val externalScope: CoroutineScope
): VoucherUseCase {
    override fun getUserVouchers(): Flow<Resource<List<Voucher>>> = voucherRepository.getUserVouchers()

    override fun getNewVoucherCount(): Flow<Int> = voucherRepository.getNewVoucherCount()

    override fun listenToPublicVouchers() = voucherRepository.listenToPublicVouchers(externalScope)

    override suspend fun applyVoucher(voucherCode: String, cartTotal: Double): Resource<Voucher> {
        if (voucherCode.isBlank()) return Resource.Error("ERROR_NO_CODE")

        val result = voucherRepository.getVoucherByCode(code = voucherCode.uppercase())
        if (result is Resource.Error) {
            return Resource.Error("ERROR_VOUCHER_NOT_FOUND")
        }

        val voucher  = result.data!!

        if (!voucher.isActive()) {
            return Resource.Error("ERROR_VOUCHER_EXPIRED_OR_USED")
        }
        if (cartTotal < voucher.minSpend) {
            val diff = voucher.minSpend - cartTotal
            return Resource.Error("Add $${diff} more to use this voucher.")
        }

        return Resource.Success(voucher)
    }

    override suspend fun syncVouchers() = voucherRepository.syncVouchers()

    override suspend fun markAllAsSeen() = voucherRepository.markAllAsSeen()

    override suspend fun clearVouchers() = voucherRepository.clearVouchers()
}