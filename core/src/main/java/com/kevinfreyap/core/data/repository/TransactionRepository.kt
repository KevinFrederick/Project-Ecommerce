package com.kevinfreyap.core.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.local.entity.TransactionEntity
import com.kevinfreyap.core.data.source.local.room.TransactionDao
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.model.order.OrderStatus
import com.kevinfreyap.core.domain.repository.ITransactionRepository
import com.kevinfreyap.core.utils.Constants.TRANSACTION_SUB_COLLECTION
import com.kevinfreyap.core.utils.Constants.USER_COLLECTION
import com.kevinfreyap.core.utils.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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

        try {
            transactionDao.insert(entity)
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
            .onStart {
                simulateOrderStatusUpdates()
            }
            .catch { e ->
                Log.e("TransactionHistory", "Failed to read order history from Room", e)
            }
            .flowOn(Dispatchers.IO)
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

    private suspend fun simulateOrderStatusUpdates() {
        val currentTime = System.currentTimeMillis()
        val updates = ArrayList<TransactionEntity>()

        val processingOrders = transactionDao.getOrdersByStatus(OrderStatus.PROCESSING)

        processingOrders.forEach { order ->
            val timeDiff = currentTime - order.datePlaced

            if (timeDiff >= DELIVERY_TIME_MS) {
                val updateOrderStatus = order.copy(orderStatus = OrderStatus.DELIVERED)
                updates.add(updateOrderStatus)
                updateFirestoreStatus(order.transactionId, OrderStatus.DELIVERED)
            } else if (timeDiff >= SHIPPED_TIME_MS) {
                val updateOrderStatus = order.copy(orderStatus = OrderStatus.SHIPPED)
                updates.add(updateOrderStatus)
                updateFirestoreStatus(order.transactionId, OrderStatus.SHIPPED)
            }
        }

        val shippedOrders = transactionDao.getOrdersByStatus(OrderStatus.SHIPPED)

        shippedOrders.forEach { order ->
            val timeDiff = currentTime - order.datePlaced

            if (timeDiff >= DELIVERY_TIME_MS) {
                val updateOrderStatus = order.copy(orderStatus = OrderStatus.DELIVERED)
                updates.add(updateOrderStatus)
                updateFirestoreStatus(order.transactionId, OrderStatus.DELIVERED)
            }
        }

        if (updates.isNotEmpty()) {
            transactionDao.insertAll(updates)
        }
    }

    private fun updateFirestoreStatus(orderId: String, status: OrderStatus) {
        val uid = firebaseAuth.currentUser?.uid ?: return

        try {
            firestore.collection(USER_COLLECTION)
                .document(uid)
                .collection(TRANSACTION_SUB_COLLECTION)
                .document(orderId)
                .update("orderStatus", status.name)
        } catch (e: Exception){
            Log.e("TransactionRepository", "Failed to update status", e)
        }
    }

    companion object {
        private const val SHIPPED_TIME_MS = 1 * 24 * 60 * 60 * 1000L
        private const val DELIVERY_TIME_MS = 2 * 24 * 60 * 60 * 1000L
    }
}