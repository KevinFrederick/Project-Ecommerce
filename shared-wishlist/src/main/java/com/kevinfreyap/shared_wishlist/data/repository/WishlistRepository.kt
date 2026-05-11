package com.kevinfreyap.shared_wishlist.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.notification.INotificationService
import com.kevinfreyap.core.utils.Constants
import com.kevinfreyap.shared_wishlist.data.mapper.WishlistDataMapper
import com.kevinfreyap.shared_wishlist.data.source.local.entity.WishlistEntity
import com.kevinfreyap.shared_wishlist.data.source.local.room.WishlistDao
import com.kevinfreyap.shared_wishlist.data.source.remote.network.WishlistApiService
import com.kevinfreyap.shared_wishlist.domain.model.FirestoreWishlistItem
import com.kevinfreyap.shared_wishlist.domain.model.WishlistItem
import com.kevinfreyap.shared_wishlist.domain.model.WishlistProduct
import com.kevinfreyap.shared_wishlist.domain.repository.IWishlistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.java

@Singleton
class WishlistRepository @Inject constructor(
    private val wishlistDao: WishlistDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val apiService: WishlistApiService,
): IWishlistRepository {
    override fun getWishlist(): Flow<Resource<List<WishlistItem>>> {
        return wishlistDao.getAllWatchlist()
            .map<List<WishlistEntity>, Resource<List<WishlistItem>>> { watchlistEntities ->
                val watchlistItems = WishlistDataMapper.mapWishlistsToDomain(watchlistEntities)
                Resource.Success(watchlistItems)
            }
            .catch { e ->
                emit(Resource.Error(e.message ?: "Failed to load watchlist"))
            }
            .flowOn(Dispatchers.IO)
    }

    override fun observeIsProductInWishlist(productId: String): Flow<Boolean> {
        return wishlistDao.isProductInWatchlist(productId).flowOn(Dispatchers.IO)
    }

    override fun addToWishlist(product: WishlistProduct): Flow<Resource<Unit>> = flow {
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) {
            emit(Resource.Error("ERROR_USER_NOT_FOUND"))
            return@flow
        }

        try {
            val currentTime = System.currentTimeMillis()

            val wishlistEntity = WishlistEntity(
                productId = product.id,
                dateAdded = currentTime,
                productCategory = product.category.name,
                productName = product.title,
                productPrice = product.price,
                productImage = product.images.firstOrNull() ?: "",
                isAvailable = true,
            )

            wishlistDao.insertWatchlist(wishlistEntity)

            val firestoreWishListItem = FirestoreWishlistItem(
                productId = product.id,
                dateAdded = currentTime,
                productName = product.title,
                productPrice = product.price,
                productImage = product.images.firstOrNull() ?: "",
                productCategory = product.category.name
            )

            firestore.collection(Constants.USER_COLLECTION)
                .document(currentUserUid)
                .collection(Constants.WISHLIST_SUB_COLLECTION)
                .document(product.id)
                .set(firestoreWishListItem)
                .addOnFailureListener {
                    Log.e("WishlistRepository", "Failed to sync wishlist ${product.id}", it)
                }

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Failed to add wishlist : ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun removeFromWishlist(productId: String) {
        try {
            wishlistDao.deleteWatchlistById(productId)

            val currentUserUid = firebaseAuth.currentUser?.uid
            if (currentUserUid.isNullOrEmpty()) {
                Log.w("WishlistRepository", "User null, skipping firestore sync")
                return
            }

            firestore.collection(Constants.USER_COLLECTION)
                .document(currentUserUid)
                .collection(Constants.WISHLIST_SUB_COLLECTION)
                .document(productId)
                .delete()
                .addOnFailureListener {
                    Log.e("WishlistRepository", "Failed to delete wishlist $productId", it)
                }
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Failed to delete wishlist: ${e.message}")
        }
    }

    override suspend fun syncWishlistOnLogin() = withContext(Dispatchers.IO) {
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) {
            Log.w("WishlistRepository", "User null, can't sync wishlist")
            return@withContext
        }

        try {
            val snapshot =
                firestore.collection(Constants.USER_COLLECTION)
                    .document(currentUserUid)
                    .collection(Constants.WISHLIST_SUB_COLLECTION)
                    .get()
                    .await()

            val wishlists = snapshot.toObjects(FirestoreWishlistItem::class.java)

            val entities = WishlistDataMapper.mapFirestoreWishlistsToEntity(wishlists)

            wishlistDao.clearWatchlist()
            wishlistDao.insertAllWatchlist(entities)
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Failed to sync wishlist", e)
        }
    }

    override suspend fun validateWishlistAvailability(): List<WishlistItem> = coroutineScope {
        val localItems = wishlistDao.getAllSync()
        if (localItems.isEmpty()) return@coroutineScope emptyList()

        val updatedEntities = localItems.map { item ->
            async {
                try {
                    apiService.getProductById(item.productId.toInt())

                    // Success: Product exist and available
                    if (!item.isAvailable && !item.isNotified) {
                        // Was Unavailable, now In Stock
                        return@async item.copy(isAvailable = true, isNotified = true)
                    }
                } catch (e: HttpException) {
                    // If Fail = Unavailable
                    if (e.code() == 404 || e.code() == 400) {
                        return@async item.copy(isAvailable = false, isNotified = false)
                    }
                } catch (e: Exception) {
                    Log.e("WishlistRepository", "Failed to validate availability", e)
                }

                return@async null
            }
        }.awaitAll().filterNotNull()

        if (updatedEntities.isNotEmpty()) {
            updatedEntities.forEach {
                wishlistDao.updateAvailability(it.productId, it.isAvailable)
                wishlistDao.updateNotificationStatus(it.productId, it.isAvailable)
            }
        }

        return@coroutineScope updatedEntities
            .filter { it.isAvailable }
            .map { WishlistDataMapper.mapWishlistEntityToDomain(it) }
    }

    override suspend fun clearWishlist() {
        try {
            wishlistDao.clearWatchlist()
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Failed to clear wishlist", e)
        }
    }
}