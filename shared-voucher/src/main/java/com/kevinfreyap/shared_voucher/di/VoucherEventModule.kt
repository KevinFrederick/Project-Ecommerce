package com.kevinfreyap.shared_voucher.di

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.shared_voucher.data.event.VoucherAuthListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class VoucherEventModule  {
    @Binds
    @IntoSet
    abstract fun bindVoucherAuthListener(
        impl: VoucherAuthListener
    ): IAuthEvenListener
}