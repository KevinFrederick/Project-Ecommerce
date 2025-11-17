package com.kevinfreyap.core.domain.usecase.transaction

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import kotlinx.coroutines.flow.Flow

interface TransactionUseCase {
    fun getTransactionHistory(): Flow<Resource<List<OrderReceipt>>>
    fun getTransactionById(transactionId: String): Flow<Resource<OrderReceipt>>
    suspend fun syncTransactionHistoryOnLogin()
    suspend fun clearOrderHistory()
}