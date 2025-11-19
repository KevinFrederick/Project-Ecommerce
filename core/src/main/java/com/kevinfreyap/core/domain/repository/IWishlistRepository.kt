package com.kevinfreyap.core.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.wishlist.WishlistItem
import kotlinx.coroutines.flow.Flow

interface IWishlistRepository {
    fun getWishlist(): Flow<Resource<List<WishlistItem>>>
    fun observeIsProductInWishlist(productId: String): Flow<Boolean>
    suspend fun addToWishlist(productId: String)
    suspend fun removeFromWishlist(productId: String)
    suspend fun syncWishlistOnLogin()
    suspend fun clearWishlist()
}