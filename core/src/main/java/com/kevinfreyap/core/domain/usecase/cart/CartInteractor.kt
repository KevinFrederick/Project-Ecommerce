package com.kevinfreyap.core.domain.usecase.cart

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.cart.CartSummary
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.repository.ICartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartInteractor @Inject constructor (
    private val cartRepository: ICartRepository,
    private val calculateSummaryService: CalculateSummaryService
): CartUseCase {
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

                calculateSummaryService(items, null)
            } else {
                CartSummary(
                    subtotal = 0,
                    shippingFee = 0,
                    voucherDiscount = 0,
                    total = 0
                )
            }
        }
    }

    override fun updateItemQuantity(
        productId: String,
        newQuantity: Int
    ): Flow<Resource<Boolean>> {
        return cartRepository.updateItemQuantity(productId, newQuantity)
    }

    override fun removeItemFromCart(productId: String): Flow<Resource<Boolean>> {
        return cartRepository.removeItemFromCart(productId)
    }

    override fun refreshCartAvailability(): Flow<Resource<Boolean>> {
        return cartRepository.refreshCartAvailability()
    }

    override suspend fun syncCartOnLogin() {
        cartRepository.syncCartOnLogin()
    }

    override suspend fun clearCart() {
        cartRepository.clearCart()
    }
}