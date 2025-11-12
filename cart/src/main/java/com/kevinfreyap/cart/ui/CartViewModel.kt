package com.kevinfreyap.cart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.usecase.cart.CartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartUseCase: CartUseCase
) : ViewModel() {

    private val _cartList = MutableStateFlow<Resource<List<Cart>>>(Resource.Loading())
    val cartList: StateFlow<Resource<List<Cart>>> = _cartList

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    init {
        loadCartList()
    }

    fun loadCartList() {
        viewModelScope.launch{
            cartUseCase.getCartItems().collect { value ->
                _cartList.value = value
            }
        }
    }

    fun increaseQuantity(cart: Cart) {
        val currentState = _cartList.value
        if (currentState !is Resource.Success) return
        val currentList = currentState.data

        val newList = currentList.map {
            if (it.product.id == cart.product.id) {
                it.copy(quantity = it.quantity + 1)
            } else {
                it
            }
        }
        _cartList.value = Resource.Success(newList)

        viewModelScope.launch {
            val newQuantity = cart.quantity + 1
            cartUseCase.updateItemQuantity(cart.product.id, newQuantity).collect { resource ->
                if (resource is Resource.Error) {
                    _cartList.value = currentState
                    _errorState.value = resource.message
                }
            }
        }
    }

    fun decreaseQuantity(cart: Cart) {
        val currentState = _cartList.value
        if (currentState !is Resource.Success) return
        val currentList = currentState.data

        // mapNotNull to easily remove the item if quantity hits 0
        val newList = currentList.mapNotNull {
            if (it.product.id == cart.product.id){
                if (it.quantity > 1) {
                    it.copy(quantity = it.quantity - 1)
                } else {
                    // Quantity is 1, so remove it (return null)
                    null
                }
            } else {
                it
            }
        }
        _cartList.value = Resource.Success(newList)

        viewModelScope.launch {
            val newQuantity = cart.quantity - 1
            if (newQuantity > 0) {
                cartUseCase.updateItemQuantity(cart.product.id, newQuantity).collect { resource ->
                    if (resource is Resource.Error) {
                        _cartList.value = currentState
                        _errorState.value = resource.message
                    }
                }
            } else {
                removeFromFireStore(cart.product.id, currentState)
            }
        }
    }

    fun removeItemFromCart(cart: Cart) {
        val currentState = _cartList.value
        if (currentState !is Resource.Success) return
        val currentList = currentState.data

        // Optimistically create new list (filter out the deleted item)
        val newList = currentList.filterNot { it.product.id == cart.product.id }
        _cartList.value = Resource.Success(newList)

        removeFromFireStore(cart.product.id, currentState)
    }

    private fun removeFromFireStore(cartId: Int, currentState: Resource<List<Cart>>) {
        viewModelScope.launch{
            cartUseCase.removeItemFromCart(cartId).collect { resource ->
                if (resource is Resource.Error) {
                    _cartList.value = currentState
                    _errorState.value = resource.message
                }
            }
        }
    }

    fun clearError() {
        _errorState.value = null
    }
}