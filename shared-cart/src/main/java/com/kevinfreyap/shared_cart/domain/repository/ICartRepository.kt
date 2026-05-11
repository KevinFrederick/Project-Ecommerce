package com.kevinfreyap.shared_cart.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_cart.domain.model.Cart
import com.kevinfreyap.shared_cart.domain.model.CartProduct
import kotlinx.coroutines.flow.Flow

interface ICartRepository {
    fun getCartItems(): Flow<Resource<List<Cart>>>
    fun getCartItemCount(): Flow<Int>
    fun addToCart(product: CartProduct, quantity: Int): Flow<Resource<Boolean>>
    fun updateItemQuantity(productId: String, newQuantity: Int): Flow<Resource<Boolean>>
    fun removeItemFromCart(productId: String): Flow<Resource<Boolean>>
    fun refreshCartAvailability(): Flow<Resource<Boolean>>
    suspend fun syncCartOnLogin()
    suspend fun clearCart()
    suspend fun clearFirestoreCart()
}