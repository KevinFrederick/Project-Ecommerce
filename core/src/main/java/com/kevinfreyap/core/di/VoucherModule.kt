package com.kevinfreyap.core.di

import com.kevinfreyap.core.domain.usecase.voucher.VoucherInteractor
import com.kevinfreyap.core.domain.usecase.voucher.VoucherUseCase
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
}