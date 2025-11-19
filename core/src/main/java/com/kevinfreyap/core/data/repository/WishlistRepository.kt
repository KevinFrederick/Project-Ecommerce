package com.kevinfreyap.core.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.local.entity.WishlistEntity
import com.kevinfreyap.core.data.source.local.room.WishlistDao
import com.kevinfreyap.core.domain.model.wishlist.WishlistItem
import com.kevinfreyap.core.domain.repository.IWishlistRepository
import com.kevinfreyap.core.utils.Constants.USER_COLLECTION
import com.kevinfreyap.core.utils.Constants.WISHLIST_SUB_COLLECTION
import com.kevinfreyap.core.utils.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishlistRepository @Inject constructor(
    private val wishlistDao: WishlistDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
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
        return wishlistDao.isProductInWatchlist(productId)
    }

    override suspend fun addToWishlist(productId: String) = withContext(Dispatchers.IO) {
        try {
            val currentTime = System.currentTimeMillis()

            val wishlistEntity = WishlistEntity(
                productId = productId,
                dateAdded = currentTime
            )

            wishlistDao.insertWatchlist(wishlistEntity)

            val currentUserUid = firebaseAuth.currentUser?.uid
            if (currentUserUid.isNullOrEmpty()) {
                Log.w("WishlistRepository", "User null, skipping firestore sync")
                return@withContext
            }

            val wishListItem = WishlistItem(
                productId = productId,
                dateAdded = currentTime
            )

            firestore.collection(USER_COLLECTION)
                .document(currentUserUid)
                .collection(WISHLIST_SUB_COLLECTION)
                .document(productId)
                .set(wishListItem)
                .addOnFailureListener {
                    Log.e("WishlistRepository", "Failed to sync wishlist $productId", it)
                }
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Failed to add wishlist : ${e.message}")
        }
    }

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

            val wishlists = snapshot.toObjects(WishlistItem::class.java)

            val entities = DataMapper.mapWishlistsDomainToEntity(wishlists)

            wishlistDao.clearWatchlist()
            wishlistDao.insertAllWatchlist(entities)
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Failed to sync wishlist", e)
        }
    }

    override suspend fun clearWishlist() {
        try {
            wishlistDao.clearWatchlist()
        } catch (e: Exception) {
            Log.e("WishlistRepository", "Failed to clear wishlist", e)
        }
    }
}