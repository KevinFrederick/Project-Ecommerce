package com.kevinfreyap.shared_cart.domain.event

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.shared_cart.domain.repository.ICartRepository
import javax.inject.Inject

class CartAuthListener @Inject constructor(
    private val cartRepository: ICartRepository
): IAuthEvenListener {
    override suspend fun onUserLoggedIn() {
        cartRepository.syncCartOnLogin()
    }

    override suspend fun onUserLoggedOut() {
        runCatching { cartRepository.clearCart() }
    }

}