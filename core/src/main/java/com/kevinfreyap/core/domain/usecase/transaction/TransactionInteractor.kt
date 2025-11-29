package com.kevinfreyap.core.domain.usecase.transaction

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionInteractor @Inject constructor(
    private val transactionRepository: ITransactionRepository
): TransactionUseCase {
    override fun getTransactionHistory(): Flow<Resource<List<OrderReceipt>>> = transactionRepository.getTransactionHistory()

    override fun getTransactionById(transactionId: String): Flow<Resource<OrderReceipt>> = transactionRepository.getTransactionById(transactionId)

    override fun listenToTransactionsUpdate() = transactionRepository.listenToTransactionUpdates()

    override suspend fun syncTransactionHistoryOnLogin() = transactionRepository.syncTransactionHistoryOnLogin()

    override suspend fun clearOrderHistory() = transactionRepository.clearOrderHistory()
}