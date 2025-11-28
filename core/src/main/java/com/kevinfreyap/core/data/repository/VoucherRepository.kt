package com.kevinfreyap.core.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.local.UserPreferences
import com.kevinfreyap.core.data.source.local.entity.VoucherEntity
import com.kevinfreyap.core.data.source.local.room.VoucherDao
import com.kevinfreyap.core.domain.model.voucher.Voucher
import com.kevinfreyap.core.domain.repository.IVoucherRepository
import com.kevinfreyap.core.domain.services.INotificationService
import com.kevinfreyap.core.utils.Constants.USER_COLLECTION
import com.kevinfreyap.core.utils.Constants.VOUCHER_COLLECTION
import com.kevinfreyap.core.utils.DataMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoucherRepository @Inject constructor(
    private val voucherDao: VoucherDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val notificationService: INotificationService,
    private val userPreferences: UserPreferences
): IVoucherRepository {
    private var listenerRegistration: ListenerRegistration? = null

    override fun getUserVouchers(): Flow<Resource<List<Voucher>>> {
        val currentTime = System.currentTimeMillis()
        return voucherDao.getAllVouchers(currentTime)
            .map <List<VoucherEntity>, Resource<List<Voucher>>> { entities ->
                val domainList = entities.map { DataMapper.mapVoucherEntityToDomain(it) }
                Resource.Success(domainList)
            }
            .catch { emit(Resource.Error("ERROR_FAILED_TO_LOAD_VOUCHERS")) }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getVoucherByCode(code: String): Resource<Voucher> = withContext(Dispatchers.IO) {
        val uid = firebaseAuth.currentUser?.uid

        try {
            val publicSnapshot = firestore.collection(VOUCHER_COLLECTION)
                .whereEqualTo("code", code)
                .limit(1)
                .get()
                .await()

            if (!publicSnapshot.isEmpty) {
                val voucher = publicSnapshot.documents.first().toObject(Voucher::class.java)!!
                return@withContext Resource.Success(voucher)
            }

            if (uid != null) {
                val privateSnapshot = firestore.collection(USER_COLLECTION)
                    .document(uid)
                    .collection(VOUCHER_COLLECTION)
                    .whereEqualTo("code", code)
                    .limit(1)
                    .get()
                    .await()

                if (privateSnapshot != null) {
                    val voucher = privateSnapshot.documents.first().toObject(Voucher::class.java)!!
                    if (voucher.type == "PRIVATE"){
                        return@withContext Resource.Success(voucher)
                    }
                }
            }

            return@withContext Resource.Error("ERROR_CODE_NOT_FOUND")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to validate voucher")
        }
    }

    override fun getNewVoucherCount(): Flow<Int> {
        return voucherDao.getNewVoucherDates()
            .map { dates ->
                val currentTime = System.currentTimeMillis()

                dates.count { expiryDate -> expiryDate > currentTime }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun listenToPublicVouchers() {
        if (listenerRegistration != null) {
            return
        }

        listenerRegistration = firestore.collection(VOUCHER_COLLECTION)
            .whereEqualTo("type", "PUBLIC")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("VoucherRepo", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    val newVouchers = snapshots.toObjects(Voucher::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        processAndSaveVouchers(newVouchers)
                    }
                }
            }
    }

    override suspend fun markVoucherAsUsed(voucher: Voucher) = withContext(Dispatchers.IO) {
        try {
            val uid = firebaseAuth.currentUser?.uid ?: return@withContext

            firestore.collection(USER_COLLECTION)
                .document(uid)
                .collection(VOUCHER_COLLECTION)
                .document(voucher.id)
                .set(voucher)
                .await()

            val entity = DataMapper.mapVoucherDomainToVoucherEntity(voucher)
            voucherDao.insertAll(listOf(entity))
        } catch (e: Exception) {
            Log.e("VoucherRepository", "Failed to mark voucher", e)
        }
    }

    override suspend fun syncVouchers() = withContext(Dispatchers.IO) {
        val uid = firebaseAuth.currentUser?.uid ?: return@withContext

        try {
            // Fetch Public
            val publicDeferred = async {
                firestore.collection(VOUCHER_COLLECTION)
                    .whereEqualTo("type", "PUBLIC")
                    .get()
                    .await()
                    .toObjects(Voucher::class.java)
            }

            // Fetch Personal
            val personalDeferred = async {
                firestore.collection(USER_COLLECTION)
                    .document(uid)
                    .collection(VOUCHER_COLLECTION)
                    .get()
                    .await()
                    .toObjects(Voucher::class.java)
            }
            val publicList = publicDeferred.await()
            val personalList = personalDeferred.await()

            // Merge (Personal Overwrites Public)
            val voucherMap = HashMap<String, Voucher>()
            publicList.forEach { voucherMap[it.id] = it }
            personalList.forEach { voucherMap[it.id] = it }
            val combinedList = voucherMap.values.toList()

            val localStateMap = voucherDao.getLocalState().associateBy { it.id }

            val entities = combinedList.map { domainModel ->
                var entity = DataMapper.mapVoucherDomainToVoucherEntity(domainModel)
                val localState = localStateMap[domainModel.id]

                entity = if (localState != null) {
                    entity.copy(
                        isNew = true,
                        isUsed = domainModel.isUsed || localState.isUsed
                    )
                } else {
                    entity.copy(isNew = true)
                }

                entity
            }

            voucherDao.insertAll(entities)
        } catch (e: Exception) {
            Log.e("VoucherRepository", "Sync Failed", e)
        }
    }

    override suspend fun markAllAsSeen() = withContext(Dispatchers.IO) {
        voucherDao.markAllAsSeen()
    }

    override suspend fun clearVouchers() = withContext(Dispatchers.IO) {
        listenerRegistration?.remove()
        listenerRegistration = null

        voucherDao.clearAll()
    }

    private suspend fun processAndSaveVouchers(incomingVouchers: List<Voucher>) {
        val localStateMap = voucherDao.getLocalState().associateBy { it.id }
        val isFirstLoad = localStateMap.isEmpty()

        val settings = userPreferences.getNotificationSettings().first()
        val arePromotionEnabled = settings.promotions

        val entities = incomingVouchers.map { voucher ->
            var entity = DataMapper.mapVoucherDomainToVoucherEntity(voucher)

            val localState = localStateMap[voucher.id]

            entity = if (localState != null) {
                entity.copy(
                    isNew = localState.isNew,
                    isUsed = localState.isUsed
                )
            } else {

                if (!isFirstLoad && arePromotionEnabled) {
                    val discountText = if (voucher.isPercentage) "${voucher.discountAmount}%" else "$${voucher.discountAmount}"

                    val title = "New Voucher! \uD83C\uDF81"
                    val message = "Get $discountText off with code: ${voucher.code}"
                    val type = "VOUCHER"
                    notificationService.showNotification(title, message, type)
                }
                entity.copy(isNew = true)
            }

            entity
        }

        voucherDao.insertAll(entities)

        val incomingIds = incomingVouchers.map { it.id }.toSet()
        voucherDao.deleteMissingPublicVouchers(incomingIds)
    }
}