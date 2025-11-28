package com.kevinfreyap.core.domain.usecase.wishlist

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.model.wishlist.WishlistItem
import com.kevinfreyap.core.domain.repository.IWishlistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WishlistInteractor @Inject constructor(
    private val wishlistRepository: IWishlistRepository
): WishlistUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getWishlist(): Flow<Resource<List<WishlistItem>>> {
        return wishlistRepository.getWishlist()
    }

    override fun observeIsProductInWishlist(productId: String): Flow<Boolean> {
        return wishlistRepository.observeIsProductInWishlist(productId)
    }

    override fun addToWishlist(product: Product): Flow<Resource<Unit>> {
        return wishlistRepository.addToWishlist(product)
    }

    override suspend fun removeFromWishlist(productId: String) {
        wishlistRepository.removeFromWishlist(productId)
    }

    override suspend fun syncWishlistOnLogin() {
        wishlistRepository.syncWishlistOnLogin()
    }

    override suspend fun validateWishlistAvailability() {
        wishlistRepository.validateWishlistAvailability()
    }

    override suspend fun clearWishlist() {
        wishlistRepository.clearWishlist()
    }
}