package com.kevinfreyap.core.domain.usecase.cart

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.cart.Cart
import kotlinx.coroutines.flow.Flow

interface CartUseCase {
    fun getCartItems(): Flow<Resource<List<Cart>>>
    fun getCartItemCount(): Flow<Int>
    fun addToCart(productId: Int, quantity: Int): Flow<Resource<Boolean>>
    fun updateItemQuantity(productId: Int, newQuantity: Int): Flow<Resource<Boolean>>
    fun removeItemFromCart(productId: Int): Flow<Resource<Boolean>>
}