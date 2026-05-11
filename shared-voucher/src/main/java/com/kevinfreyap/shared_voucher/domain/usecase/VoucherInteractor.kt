package com.kevinfreyap.shared_voucher.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_voucher.domain.model.Voucher
import com.kevinfreyap.shared_voucher.domain.repository.IVoucherRepository
import javax.inject.Inject

class VoucherInteractor @Inject constructor(
    private val voucherRepository: IVoucherRepository,
): VoucherUseCase {

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

    override suspend fun clearVouchers() = voucherRepository.clearVouchers()
}