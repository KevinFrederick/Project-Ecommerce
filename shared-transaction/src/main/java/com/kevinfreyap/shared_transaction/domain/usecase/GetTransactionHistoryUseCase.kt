package com.kevinfreyap.shared_transaction.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import com.kevinfreyap.shared_transaction.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionHistoryUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository
) {
    operator fun invoke(): Flow<Resource<List<TransactionReceipt>>> = transactionRepository.getTransactionHistory()
}