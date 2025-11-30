package com.kevinfreyap.core.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.local.entity.WishlistEntity
import com.kevinfreyap.core.data.source.local.room.WishlistDao
import com.kevinfreyap.core.data.source.remote.network.ApiService
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.model.wishlist.FirestoreWishlistItem
import com.kevinfreyap.core.domain.model.wishlist.WishlistItem
import com.kevinfreyap.core.domain.repository.IWishlistRepository
import com.kevinfreyap.core.utils.Constants.USER_COLLECTION
import com.kevinfreyap.core.utils.Constants.WISHLIST_SUB_COLLECTION
import com.kevinfreyap.core.utils.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

@Singleton
class WishlistRepository @Inject constructor(
    private val wishlistDao: WishlistDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val apiService: ApiService
): IWishlistRepository {
    override fun getWishlist(): Flow<Resource<List<WishlistItem>>> {
        return wishlistDao.getAllWatchlist()
            .map <List<WishlistEntity>, Resource<List<WishlistItem>>> { watchlistEntities ->
                val watchlistItems = DataMapper.mapWishlistsToDomain(watchlistEntities)
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

    override fun addToWishlist(product: Product): Flow<Resource<Unit>> = flow {
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

            firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(WISHLIST_SUB_COLLECTION)
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

            firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(WISHLIST_SUB_COLLECTION)
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
                firestore.collection(USER_COLLECTION)
                    .document(currentUserUid)
                    .collection(WISHLIST_SUB_COLLECTION)
                    .get()
                    .await()

            val wishlists = snapshot.toObjects(FirestoreWishlistItem::class.java)

            val entities = DataMapper.mapFirestoreWishlistsToEntity(wishlists)

            wishlistDao.clearWatchlist()
            wishlistDao.insertAllWatchlist(entities)

            validateWishlistAvailability()
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Failed to sync wishlist", e)
        }
    }

    override suspend fun validateWishlistAvailability() = withContext(Dispatchers.IO) {
        val localItems = wishlistDao.getAllSync()
        if (localItems.isEmpty()) return@withContext

        localItems.map { item ->
            async {
                try {
                    apiService.getProductById(item.productId.toInt())
                    if (!item.isAvailable && !item.isNotified) {
                        wishlistDao.updateAvailability(item.productId, true)
                        wishlistDao.updateNotificationStatus(item.productId, true)
                    }
                } catch (e: HttpException) {
                    if (e.code() == 404 || e.code() == 400) {
                        if (item.isAvailable) {
                            wishlistDao.updateAvailability(item.productId, false)
                            wishlistDao.updateNotificationStatus(item.productId, false)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WishlistRepository", "Failed to validate availability", e)
                }
            }
        }.awaitAll()
    }

    override suspend fun clearWishlist() {
        try {
            wishlistDao.clearWatchlist()
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Failed to clear wishlist", e)
        }
    }
}