package com.kevinfreyap.shared_wishlist.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_wishlist.domain.model.WishlistItem
import com.kevinfreyap.shared_wishlist.domain.repository.IWishlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWishlistUseCase @Inject constructor(
    private val wishlistRepository: IWishlistRepository
) {
    operator fun invoke(): Flow<Resource<List<WishlistItem>>> {
        return wishlistRepository.getWishlist()
    }
}