package com.kevinfreyap.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kevinfreyap.core.data.source.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: TransactionEntity)

    @Query("SELECT * FROM transaction_history ORDER BY datePlaced DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transaction_history WHERE transactionId = :id LIMIT 1")
    fun getTransactionById(id: String): Flow<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<TransactionEntity>)

    @Query("DELETE FROM transaction_history WHERE transactionId = :id")
    suspend fun deleteTransactionById(id: String)

    @Query("DELETE FROM transaction_history")
    suspend fun clearAll()
}