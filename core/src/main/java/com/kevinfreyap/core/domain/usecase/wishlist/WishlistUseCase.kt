package com.kevinfreyap.core.domain.usecase.wishlist

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.model.wishlist.WishlistItem
import kotlinx.coroutines.flow.Flow

interface WishlistUseCase {
    fun getWishlist(): Flow<Resource<List<WishlistItem>>>
    fun observeIsProductInWishlist(productId: String): Flow<Boolean>
    fun addToWishlist(product: Product): Flow<Resource<Unit>>
    suspend fun removeFromWishlist(productId: String)
    suspend fun syncWishlistOnLogin()
    suspend fun validateWishlistAvailability()
    suspend fun clearWishlist()
}