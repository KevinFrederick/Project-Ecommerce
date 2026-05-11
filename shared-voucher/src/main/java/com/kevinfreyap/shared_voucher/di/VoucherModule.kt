package com.kevinfreyap.shared_voucher.di

import com.kevinfreyap.shared_voucher.data.repository.VoucherRepository
import com.kevinfreyap.shared_voucher.domain.repository.IVoucherRepository
import com.kevinfreyap.shared_voucher.domain.usecase.VoucherInteractor
import com.kevinfreyap.shared_voucher.domain.usecase.VoucherUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class VoucherModule {
    @Binds
    @Singleton
    abstract fun provideVoucherUseCase(voucherInteractor: VoucherInteractor): VoucherUseCase

    @Binds
    @Singleton
    abstract fun provideVoucherRepository(voucherRepository: VoucherRepository): IVoucherRepository
}