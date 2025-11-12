package com.kevinfreyap.core.domain.usecase.cart

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.repository.CartRepository
import com.kevinfreyap.core.domain.model.cart.Cart
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CartInteractor @Inject constructor (private val cartRepository: CartRepository): CartUseCase {
    override fun getCartItems(): Flow<Resource<List<Cart>>> = cartRepository.getCartItems()
    override fun addToCart(
        productId: Int,
        quantity: Int
    ): Flow<Resource<Boolean>> {
        return cartRepository.addToCart(productId, quantity)
    }

    override fun getCartItemCount(): Flow<Int> = cartRepository.getCartItemCount()

    override fun updateItemQuantity(
        productId: Int,
        newQuantity: Int
    ): Flow<Resource<Boolean>> {
        return cartRepository.updateItemQuantity(productId, newQuantity)
    }

    override fun removeItemFromCart(productId: Int): Flow<Resource<Boolean>> {
        return cartRepository.removeItemFromCart(productId)
    }
}