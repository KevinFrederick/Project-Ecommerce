package com.kevinfreyap.shared_wishlist.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_wishlist.domain.model.WishlistItem
import com.kevinfreyap.shared_wishlist.domain.model.WishlistProduct
import com.kevinfreyap.shared_wishlist.domain.repository.IWishlistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WishlistInteractor @Inject constructor(
    private val wishlistRepository: IWishlistRepository
): WishlistUseCase {

    override suspend fun syncWishlistOnLogin() {
        wishlistRepository.syncWishlistOnLogin()
    }

    override suspend fun clearWishlist() {
        wishlistRepository.clearWishlist()
    }
}