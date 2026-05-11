package com.kevinfreyap.shared_wishlist.data.event

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.shared_wishlist.domain.repository.IWishlistRepository
import javax.inject.Inject

class WishlistAuthListener @Inject constructor(
    private val wishlistRepository: IWishlistRepository
): IAuthEvenListener {
    override suspend fun onUserLoggedIn() {
        wishlistRepository.syncWishlistOnLogin()
    }

    override suspend fun onUserLoggedOut() {
        runCatching { wishlistRepository.clearWishlist() }
    }
}