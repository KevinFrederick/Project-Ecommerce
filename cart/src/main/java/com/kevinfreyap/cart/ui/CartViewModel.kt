package com.kevinfreyap.cart.ui

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.cart.utils.CheckoutActionState
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_cart.domain.model.Cart
import com.kevinfreyap.shared_cart.domain.model.CartSummary
import com.kevinfreyap.shared_cart.domain.usecase.GetCartItemUseCase
import com.kevinfreyap.shared_cart.domain.usecase.GetCartSummaryUseCase
import com.kevinfreyap.shared_cart.domain.usecase.RefreshCartAvailabilityUseCase
import com.kevinfreyap.shared_cart.domain.usecase.RemoveCartItemUseCase
import com.kevinfreyap.shared_cart.domain.usecase.UpdateItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    getCartItems: GetCartItemUseCase,
    getCartSummary: GetCartSummaryUseCase,
    private val refreshCartAvailability: RefreshCartAvailabilityUseCase,
    private val updateItemQuantity: UpdateItemQuantityUseCase,
    private val removeCartItem: RemoveCartItemUseCase,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {
    val cartList: StateFlow<Resource<List<Cart>>> = getCartItems()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading()
        )

    private val _checkoutState = MutableStateFlow<CheckoutActionState>(CheckoutActionState.Idle)
    val checkoutState: StateFlow<CheckoutActionState> = _checkoutState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    val cartSummary: StateFlow<CartSummary> = getCartSummary()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartSummary(0, 0, 0, 0)
        )

    init {
        refreshCart()
    }

    fun refreshCart() {
        viewModelScope.launch {
            refreshCartAvailability().collect { resource ->
                if (resource is Resource.Error){
                    _errorState.value = resource.message
                }
            }
        }
    }

    fun increaseQuantity(cart: Cart) {
        viewModelScope.launch {
            val newQuantity = cart.quantity + 1
            updateItemQuantity(cart.product.id, newQuantity).collect { resource ->
                if (resource is Resource.Error) {
                    _errorState.value = resource.message
                }
            }
        }
    }

    fun decreaseQuantity(cart: Cart) {
        viewModelScope.launch {
            val newQuantity = cart.quantity - 1
            if (newQuantity > 0) {
                updateItemQuantity(cart.product.id, newQuantity).collect { resource ->
                    if (resource is Resource.Error) {
                        _errorState.value = resource.message
                    }
                }
            } else {
                removeItemFromCart(cart)
            }
        }
    }

    fun removeItemFromCart(cart: Cart) {
        viewModelScope.launch{
            removeCartItem(cart.product.id).collect { resource ->
                if (resource is Resource.Error) {
                    _errorState.value = resource.message
                }
            }
        }
    }

    fun onCheckoutClicked() {
        if (_checkoutState.value is CheckoutActionState.Loading) return

        if (!isInternetAvailable()) {
            _errorState.value = "ERROR_NO_CONNECTION"
            return
        }

        viewModelScope.launch {
            refreshCartAvailability().collect { resource ->
                when(resource) {
                    is Resource.Loading -> {
                        _checkoutState.value = CheckoutActionState.Loading
                    }
                    is Resource.Success -> {
                        val isCartValid = resource.data
                        if (isCartValid) {
                            _checkoutState.value = CheckoutActionState.Navigate
                        } else {
                            _errorState.value = "ERROR_REMOVE_UNAVAILABLE_ITEM"
                            _checkoutState.value = CheckoutActionState.Idle
                        }
                    }
                    is Resource.Error -> {
                        _errorState.value = resource.message ?: "Failed to Check"
                        _checkoutState.value = CheckoutActionState.Idle
                    }
                }
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun clearError() {
        _errorState.value = null
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutActionState.Idle
    }
}