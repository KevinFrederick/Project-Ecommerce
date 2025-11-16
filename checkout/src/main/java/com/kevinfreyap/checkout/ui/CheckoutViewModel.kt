package com.kevinfreyap.checkout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.checkout.utils.OrderState
import com.kevinfreyap.core.utils.PaymentMethod
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.cart.CartSummary
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.usecase.cart.CartUseCase
import com.kevinfreyap.core.domain.usecase.order.OrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartUseCase: CartUseCase,
    private val orderUseCase: OrderUseCase
): ViewModel() {
    private val _orderState = MutableStateFlow<OrderState>(OrderState.Idle)
    val orderState: StateFlow<OrderState> = _orderState

    private val _selectedAddress = MutableStateFlow(
        UserAddress(
            street = "123 Main Street, Apt 4",
            city = "Anytown, CA",
            zipCode = "91234"
        )
    )

    private val _selectedMethod = MutableStateFlow(PaymentMethod.CASH)
    val selectedMethod: StateFlow<PaymentMethod> = _selectedMethod

    val cartList: StateFlow<Resource<List<Cart>>> = cartUseCase.getCartItems()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading()
        )

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    val cartSummary: StateFlow<CartSummary> = cartUseCase.getCartSummary()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartSummary(0, 0, 0, 0)
        )

    fun selectMethod(method: PaymentMethod) {
        _selectedMethod.value = method
    }

    fun increaseQuantity(cart: Cart) {
        viewModelScope.launch {
            val newQuantity = cart.quantity + 1
            cartUseCase.updateItemQuantity(cart.product.id, newQuantity).collect { resource ->
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
                cartUseCase.updateItemQuantity(cart.product.id, newQuantity).collect { resource ->
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
            cartUseCase.removeItemFromCart(cart.product.id).collect { resource ->
                if (resource is Resource.Error) {
                    _errorState.value = resource.message
                }
            }
        }
    }

    fun onOrderClicked() {
        if (_orderState.value is OrderState.Loading) return

        viewModelScope.launch {
            _orderState.value = OrderState.Loading

            val result = orderUseCase.placeOrder(
                address = _selectedAddress.value,
                paymentMethod = _selectedMethod.value,
                voucher = ""
            )

            if (result is Resource.Success) {
                _orderState.value = OrderState.OrderSuccess(result.data)
            } else {
                _errorState.value = result.message
                _orderState.value = OrderState.Idle
            }
        }
    }

    fun clearError() {
        _errorState.value = null
    }

    fun resetOrderState() {
        _orderState.value = OrderState.Idle
    }
}