package com.kevinfreyap.shared_transaction.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.notification.INotificationService
import com.kevinfreyap.core.utils.Constants
import com.kevinfreyap.shared_transaction.data.mapper.TransactionDataMapper
import com.kevinfreyap.shared_transaction.data.source.local.entity.TransactionEntity
import com.kevinfreyap.shared_transaction.data.source.local.room.TransactionDao
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import com.kevinfreyap.shared_transaction.domain.model.TransactionStatus
import com.kevinfreyap.shared_transaction.domain.repository.ITransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.collections.map

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
): ITransactionRepository {

    private var transactionListener: ListenerRegistration? = null

    override suspend fun saveTransaction(receipt: TransactionReceipt) {
        val entity = TransactionDataMapper.mapOrderDomainToEntity(receipt)

        try {
            transactionDao.insert(entity)
            val currentUserUid = firebaseAuth.currentUser?.uid
            if (currentUserUid.isNullOrEmpty()) {
                Log.w("TransactionRepository", "User is null, skipping Firestore sync")
                return
            }

            firestore.collection(Constants.USER_COLLECTION)
                .document(currentUserUid)
                .collection(Constants.TRANSACTION_SUB_COLLECTION)
                .document(receipt.orderId)
                .set(receipt)
                .addOnFailureListener {
                    Log.e("TransactionHistory", "Failed to sync order ${receipt.orderId}", it)
                }
        } catch (e: Exception){
            Log.e("TransactionRepository", "Failed to save order: ${e.message}")
        }
    }

    override fun getTransactionHistory(): Flow<Resource<List<TransactionReceipt>>> {
        return transactionDao.getAllTransactions()
            .map { entities ->
                val receipts = TransactionDataMapper.mapTransactionsEntityToDomain(entities)
                Resource.Success(receipts)
            }
            .catch { e ->
                Log.e("TransactionHistory", "Failed to read order history from Room", e)
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getTransactionById(transactionId: String): Flow<Resource<TransactionReceipt>> {
        return transactionDao.getTransactionById(transactionId)
            .map { entity ->
                val receipt = TransactionDataMapper.mapTransactionEntityToDomain(entity)
                Resource.Success(receipt)
            }
            .catch { e ->
                Log.e("TransactionRepository", "Failed to get order by Id", e)
            }
    }

    override fun listenToTransactionUpdates() {
        if (transactionListener != null) return
        val uid = firebaseAuth.currentUser?.uid ?: return

        transactionListener = firestore.collection(Constants.USER_COLLECTION)
            .document(uid)
            .collection(Constants.TRANSACTION_SUB_COLLECTION)
            .addSnapshotListener { snapshots, error ->
                if (error != null){
                    Log.w("TransactionRepository", "Listen failed", error)
                    return@addSnapshotListener
                }

                if (snapshots != null){

                    CoroutineScope(Dispatchers.IO).launch {
                        val updates = ArrayList<TransactionEntity>()

                        for (change in snapshots.documentChanges) {
                            val doc = change.document
                            val order = doc.toObject(TransactionReceipt::class.java)
                            val entity = TransactionDataMapper.mapOrderDomainToEntity(order)

                            updates.add(entity)
                        }

                        if (updates.isNotEmpty()) {
                            transactionDao.insertAll(updates)
                        }
                    }
                }
            }
    }

    override suspend fun syncTransactionHistoryOnLogin() {
        val currentUserUid = firebaseAuth.currentUser?.uid ?: return

        try {
            val snapshot = firestore.collection(Constants.USER_COLLECTION)
                .document(currentUserUid)
                .collection(Constants.TRANSACTION_SUB_COLLECTION)
                .orderBy("datePlaced", Query.Direction.DESCENDING)
                .get()
                .await()

            val receipts = snapshot.toObjects(TransactionReceipt::class.java)

            val entities = receipts.map { receipt ->
                TransactionDataMapper.mapOrderDomainToEntity(receipt)
            }

            transactionDao.clearAll()
            transactionDao.insertAll(entities)
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Failed to sync transaction history", e)
        }
    }

    override suspend fun clearTransactionHistory() {
        try {
            transactionListener?.remove()
            transactionListener = null

            transactionDao.clearAll()
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Failed to clear history", e)
        }
    }

    override suspend fun simulateOrderStatusUpdates(): List<TransactionReceipt> {
        val currentTime = System.currentTimeMillis()
        val processingOrders = transactionDao.getOrdersByStatus(TransactionStatus.PROCESSING)

        val updates = ArrayList<TransactionEntity>()
        val changedOrdersDomain = mutableListOf<TransactionReceipt>()

        processingOrders.forEach { order ->
            val timeDiff = currentTime - order.datePlaced

            // 1. Determine the new status (if any)
            val newStatus = when {
                timeDiff >= DELIVERY_TIME_MS -> TransactionStatus.DELIVERED
                timeDiff >= SHIPPED_TIME_MS -> TransactionStatus.SHIPPED
                else -> null
            }

            // 2. If the status changed, process the update
            if (newStatus != null) {
                // Update the remote database
                updateFirestoreStatus(order.transactionId, newStatus)

                // Prepare the local Room entity update
                val updatedEntity = order.copy(transactionStatus = newStatus)
                updates.add(updatedEntity)

                // Convert to your Domain model to hand back to the Use Case
                changedOrdersDomain.add(TransactionDataMapper.mapTransactionEntityToDomain(updatedEntity))
            }
        }

        // 3. Save all local updates to Room in a single batch transaction!
        if (updates.isNotEmpty()) {
            transactionDao.insertAll(updates)
        }

        // 4. Return ONLY the items that changed (The Diff)
        return changedOrdersDomain
    }

    private fun updateFirestoreStatus(orderId: String, status: TransactionStatus) {
        val uid = firebaseAuth.currentUser?.uid ?: return

        try {
            firestore.collection(Constants.USER_COLLECTION)
                .document(uid)
                .collection(Constants.TRANSACTION_SUB_COLLECTION)
                .document(orderId)
                .update("TransactionStatus", status.name)
        } catch (e: Exception){
            Log.e("TransactionRepository", "Failed to update status", e)
        }
    }

    companion object {
        private const val SHIPPED_TIME_MS = 1 * 12 * 60 * 60 * 1000L
        private const val DELIVERY_TIME_MS = 2 * 7 * 60 * 60 * 1000L
    }
}