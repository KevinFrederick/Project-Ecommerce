package com.kevinfreyap.shared_wishlist.domain.usecase

import com.kevinfreyap.shared_wishlist.domain.repository.IWishlistRepository
import javax.inject.Inject

class RemoveFromWishlistUseCase @Inject constructor(
    private val wishlistRepository: IWishlistRepository
) {
    suspend operator fun invoke(productId: String) {
        wishlistRepository.removeFromWishlist(productId)
    }
}