package com.kevinfreyap.core.di

import com.kevinfreyap.core.domain.usecase.transaction.TransactionInteractor
import com.kevinfreyap.core.domain.usecase.transaction.TransactionUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TransactionModule {
    @Binds
    @Singleton
    abstract fun provideTransactionUseCase(transactionInteractor: TransactionInteractor): TransactionUseCase
}