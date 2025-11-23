package com.kevinfreyap.core.domain.usecase.wishlist

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import kotlinx.coroutines.flow.Flow

interface WishlistUseCase {
    fun getWishlist(): Flow<Resource<List<Product>>>
    fun observeIsProductInWishlist(productId: String): Flow<Boolean>
    fun addToWishlist(productId: String): Flow<Resource<Unit>>
    suspend fun removeFromWishlist(productId: String)
    suspend fun syncWishlistOnLogin()
    suspend fun clearWishlist()
}