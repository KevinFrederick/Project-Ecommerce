package com.kevinfreyap.shared_transaction.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import kotlinx.coroutines.flow.Flow

interface ITransactionRepository {
    suspend fun saveTransaction(receipt: TransactionReceipt)
    fun getTransactionHistory(): Flow<Resource<List<TransactionReceipt>>>
    fun getTransactionById(transactionId: String): Flow<Resource<TransactionReceipt>>
    fun listenToTransactionUpdates()
    suspend fun simulateOrderStatusUpdates(): List<TransactionReceipt>
    suspend fun syncTransactionHistoryOnLogin()
    suspend fun clearTransactionHistory()
}