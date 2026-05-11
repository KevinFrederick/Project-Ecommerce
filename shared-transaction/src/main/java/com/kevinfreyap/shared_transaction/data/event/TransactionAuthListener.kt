package com.kevinfreyap.shared_transaction.data.event

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.shared_transaction.domain.repository.ITransactionRepository
import javax.inject.Inject

class TransactionAuthListener @Inject constructor(
    private val transactionRepository: ITransactionRepository
): IAuthEvenListener {
    override suspend fun onUserLoggedIn() {
        transactionRepository.syncTransactionHistoryOnLogin()
    }

    override suspend fun onUserLoggedOut() {
        runCatching { transactionRepository.clearTransactionHistory() }
    }

}