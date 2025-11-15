package com.kevinfreyap.core.domain.usecase.cart

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.repository.CartRepository
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.cart.CartSummary
import com.kevinfreyap.core.domain.model.product.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartInteractor @Inject constructor (private val cartRepository: CartRepository): CartUseCase {
    override fun getCartItems(): Flow<Resource<List<Cart>>> = cartRepository.getCartItems()
    override fun addToCart(
        product: Product,
        quantity: Int
    ): Flow<Resource<Boolean>> {
        return cartRepository.addToCart(product, quantity)
    }

    override fun getCartItemCount(): Flow<Int> = cartRepository.getCartItemCount()

    override fun getCartSummary(): Flow<CartSummary> {
        return cartRepository.getCartItems().map { resource ->
            if (resource is Resource.Success) {
                val items = resource.data.filter { cart ->
                    cart.isAvailable
                }

                val subtotal = items.sumOf { it.product.price.toDouble() * it.quantity }
                val shippingFee = if (0 < subtotal && subtotal < 100) 20.0 else 0.0
                val total = subtotal + shippingFee

                CartSummary(
                    subtotal = subtotal.toInt(),
                    shippingFee = shippingFee.toInt(),
                    total = total.toInt()
                )
            } else {
                CartSummary(
                    subtotal = 0,
                    shippingFee = 0,
                    total = 0
                )
            }
        }
    }

    override fun updateItemQuantity(
        productId: Int,
        newQuantity: Int
    ): Flow<Resource<Boolean>> {
        return cartRepository.updateItemQuantity(productId, newQuantity)
    }

    override fun removeItemFromCart(productId: Int): Flow<Resource<Boolean>> {
        return cartRepository.removeItemFromCart(productId)
    }

    override fun refreshCartAvailability(): Flow<Resource<Boolean>> {
        return cartRepository.refreshCartAvailability()
    }

    override suspend fun syncCartOnLogin() {
        cartRepository.syncCartOnLogin()
    }

    override suspend fun clearCartOnLogout() {
        cartRepository.clearCartOnLogout()
    }
}