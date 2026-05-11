package com.kevinfreyap.shared_transaction.domain.usecase

import com.kevinfreyap.shared_transaction.domain.repository.ITransactionRepository
import javax.inject.Inject

class TransactionInteractor @Inject constructor(
    private val transactionRepository: ITransactionRepository
): TransactionUseCase {
    override suspend fun syncTransactionHistoryOnLogin() = transactionRepository.syncTransactionHistoryOnLogin()

    override suspend fun clearTransactionHistory() = transactionRepository.clearTransactionHistory()
}