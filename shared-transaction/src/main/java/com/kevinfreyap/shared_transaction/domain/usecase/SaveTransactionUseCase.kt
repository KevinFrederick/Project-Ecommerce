package com.kevinfreyap.shared_transaction.domain.usecase

import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import com.kevinfreyap.shared_transaction.domain.repository.ITransactionRepository
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository
) {
    suspend operator fun invoke(receipt: TransactionReceipt) {
        transactionRepository.saveTransaction(receipt)
    }
}