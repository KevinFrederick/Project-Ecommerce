package com.kevinfreyap.shared_wishlist.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_wishlist.domain.model.WishlistItem
import com.kevinfreyap.shared_wishlist.domain.model.WishlistProduct
import kotlinx.coroutines.flow.Flow

interface WishlistUseCase {
    suspend fun syncWishlistOnLogin()
    suspend fun clearWishlist()
}