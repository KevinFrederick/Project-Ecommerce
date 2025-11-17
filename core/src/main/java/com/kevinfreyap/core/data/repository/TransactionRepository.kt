package com.kevinfreyap.core.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.local.room.TransactionDao
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.repository.ITransactionRepository
import com.kevinfreyap.core.utils.Constants.TRANSACTION_SUB_COLLECTION
import com.kevinfreyap.core.utils.Constants.USER_COLLECTION
import com.kevinfreyap.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): ITransactionRepository {
    override suspend fun saveOrder(receipt: OrderReceipt) {
        val entity = DataMapper.mapOrderDomainToEntity(receipt)

        transactionDao.insert(entity)
        try {
            val currentUserUid = firebaseAuth.currentUser?.uid
            if (currentUserUid.isNullOrEmpty()) {
                Log.w("TransactionRepository", "User is null, skipping Firestore sync")
                return
            }

            firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(TRANSACTION_SUB_COLLECTION)
                .document(receipt.orderId)
                .set(receipt)
                .addOnFailureListener {
                    Log.e("TransactionHistory", "Failed to sync order ${receipt.orderId}", it)
                }
        } catch (e: Exception){
            Log.e("TransactionRepository", "Failed to save order: ${e.message}")
        }
    }

    override fun getTransactionHistory(): Flow<Resource<List<OrderReceipt>>> {
        return transactionDao.getAllTransactions()
            .map { entities ->
                val receipts = DataMapper.mapTransactionsEntityToDomain(entities)
                Resource.Success(receipts)
            }
            .catch { e ->
                Log.e("TransactionHistory", "Failed to read order history from Room", e)
            }
    }

    override fun getTransactionById(transactionId: String): Flow<Resource<OrderReceipt>> {
        return transactionDao.getTransactionById(transactionId)
            .map { entity ->
                val receipt = DataMapper.mapTransactionEntityToDomain(entity)
                Resource.Success(receipt)
            }
            .catch { e ->
                Log.e("TransactionRepository", "Failed to get order by Id", e)
            }
    }

    override suspend fun syncTransactionHistoryOnLogin() {
        val currentUserUid = firebaseAuth.currentUser?.uid ?: return

        try {
            val snapshot = firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(TRANSACTION_SUB_COLLECTION)
                .orderBy("datePlaced", Query.Direction.DESCENDING)
                .get()
                .await()

            val receipts = snapshot.toObjects(OrderReceipt::class.java)

            val entities = receipts.map { receipt ->
                DataMapper.mapOrderDomainToEntity(receipt)
            }

            transactionDao.clearAll()
            transactionDao.insertAll(entities)
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Failed to sync transaction history", e)
        }
    }

    override suspend fun clearOrderHistory() {
        try {
            transactionDao.clearAll()
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Failed to clear history", e)
        }
    }
}