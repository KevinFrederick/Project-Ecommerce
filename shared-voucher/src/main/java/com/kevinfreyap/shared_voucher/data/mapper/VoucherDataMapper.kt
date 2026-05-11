package com.kevinfreyap.shared_voucher.data.mapper

import com.kevinfreyap.shared_voucher.data.source.local.entity.VoucherEntity
import com.kevinfreyap.shared_voucher.domain.model.Voucher

object VoucherDataMapper {
    // Voucher
    fun mapVoucherEntityToDomain(entity: VoucherEntity): Voucher {
        return Voucher(
            id = entity.id,
            code = entity.code,
            title = entity.title,
            description = entity.description,
            discountAmount = entity.discountAmount,
            isPercentage = entity.isPercentage,
            minSpend = entity.minSpend,
            expiryDate = entity.expiryDate,
            type = entity.type,
            isUsed = entity.isUsed,
            isNew = entity.isNew
        )
    }

    fun mapVoucherDomainToVoucherEntity(domain: Voucher): VoucherEntity {
        return VoucherEntity(
            id = domain.id,
            code = domain.code,
            title = domain.title,
            description = domain.description,
            discountAmount = domain.discountAmount,
            isPercentage = domain.isPercentage,
            minSpend = domain.minSpend,
            expiryDate = domain.expiryDate,
            type = domain.type,
            isUsed = domain.isUsed,
            isNew = domain.isNew
        )
    }
}