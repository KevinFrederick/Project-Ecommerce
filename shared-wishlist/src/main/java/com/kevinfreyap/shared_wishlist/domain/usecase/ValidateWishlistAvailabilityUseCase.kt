package com.kevinfreyap.shared_wishlist.domain.usecase

import com.kevinfreyap.shared_wishlist.domain.repository.IWishlistRepository
import javax.inject.Inject

class ValidateWishlistAvailabilityUseCase @Inject constructor(
    private val wishlistRepository: IWishlistRepository
) {
    suspend operator fun invoke() {
        wishlistRepository.validateWishlistAvailability()
    }
}