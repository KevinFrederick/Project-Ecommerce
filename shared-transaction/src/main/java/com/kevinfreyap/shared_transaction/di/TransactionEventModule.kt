package com.kevinfreyap.shared_transaction.di

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.shared_transaction.data.event.TransactionAuthListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class TransactionEventModule {
    @Binds
    @IntoSet
    abstract fun bindTransactionAuthListener(
        impl: TransactionAuthListener
    ): IAuthEvenListener
}