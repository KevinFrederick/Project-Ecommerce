package com.kevinfreyap.core.domain.usecase.cart

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.cart.CartSummary
import com.kevinfreyap.core.domain.model.product.Product
import kotlinx.coroutines.flow.Flow

interface CartUseCase {
    fun getCartItems(): Flow<Resource<List<Cart>>>
    fun getCartItemCount(): Flow<Int>
    fun getCartSummary(): Flow<CartSummary>
    fun addToCart(product: Product, quantity: Int): Flow<Resource<Boolean>>
    fun updateItemQuantity(productId: Int, newQuantity: Int): Flow<Resource<Boolean>>
    fun removeItemFromCart(productId: Int): Flow<Resource<Boolean>>
    fun refreshCartAvailability(): Flow<Resource<Boolean>>
    suspend fun syncCartOnLogin()
    suspend fun clearCart()
}