package com.kevinfreyap.core.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import kotlinx.coroutines.flow.Flow

interface ITransactionRepository {
    suspend fun saveOrder(receipt: OrderReceipt)
    fun getTransactionHistory(): Flow<Resource<List<OrderReceipt>>>
    fun getTransactionById(transactionId: String): Flow<Resource<OrderReceipt>>
    fun listenToTransactionUpdates()
    suspend fun simulateOrderStatusUpdates()
    suspend fun syncTransactionHistoryOnLogin()
    suspend fun clearOrderHistory()
}