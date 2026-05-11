package com.kevinfreyap.shared_wishlist.domain.usecase

import com.kevinfreyap.shared_wishlist.domain.repository.IWishlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsProductInWishlistUseCase @Inject constructor(
    private val wishlistRepository: IWishlistRepository
) {
    operator fun invoke(productId: String): Flow<Boolean> {
        return wishlistRepository.observeIsProductInWishlist(productId)
    }
}