package com.kevinfreyap.shared_transaction.domain.usecase

interface TransactionUseCase {
    suspend fun syncTransactionHistoryOnLogin()
    suspend fun clearTransactionHistory()
}