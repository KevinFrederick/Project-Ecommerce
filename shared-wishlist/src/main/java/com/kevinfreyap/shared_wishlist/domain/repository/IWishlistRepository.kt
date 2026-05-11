package com.kevinfreyap.shared_wishlist.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_wishlist.domain.model.WishlistItem
import com.kevinfreyap.shared_wishlist.domain.model.WishlistProduct
import kotlinx.coroutines.flow.Flow

interface IWishlistRepository {
    fun getWishlist(): Flow<Resource<List<WishlistItem>>>
    fun observeIsProductInWishlist(productId: String): Flow<Boolean>
    fun addToWishlist(product: WishlistProduct): Flow<Resource<Unit>>
    suspend fun removeFromWishlist(productId: String)
    suspend fun syncWishlistOnLogin()
    suspend fun validateWishlistAvailability(): List<WishlistItem>
    suspend fun clearWishlist()
}