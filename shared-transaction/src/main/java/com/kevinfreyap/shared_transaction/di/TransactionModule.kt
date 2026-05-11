package com.kevinfreyap.shared_transaction.di

import com.kevinfreyap.shared_transaction.data.repository.TransactionRepository
import com.kevinfreyap.shared_transaction.domain.repository.ITransactionRepository
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
    abstract fun provideTransactionRepository(transactionRepository: TransactionRepository): ITransactionRepository
}