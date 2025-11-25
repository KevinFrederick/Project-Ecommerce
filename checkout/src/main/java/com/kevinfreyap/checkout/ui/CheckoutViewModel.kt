package com.kevinfreyap.checkout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.checkout.utils.OrderState
import com.kevinfreyap.core.utils.PaymentMethod
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.cart.Cart
import com.kevinfreyap.core.domain.model.cart.CartSummary
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.voucher.Voucher
import com.kevinfreyap.core.domain.usecase.auth.AuthUseCase
import com.kevinfreyap.core.domain.usecase.cart.CalculateSummaryService
import com.kevinfreyap.core.domain.usecase.cart.CartUseCase
import com.kevinfreyap.core.domain.usecase.order.OrderUseCase
import com.kevinfreyap.core.domain.usecase.voucher.VoucherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    authUseCase: AuthUseCase,
    private val cartUseCase: CartUseCase,
    private val calculateSummaryService: CalculateSummaryService,
    private val orderUseCase: OrderUseCase,
    private val voucherUseCase: VoucherUseCase
): ViewModel() {
    private val _orderState = MutableStateFlow<OrderState>(OrderState.Idle)
    val orderState: StateFlow<OrderState> = _orderState

    val userAddress: StateFlow<UserAddress?> = authUseCase.getUserProfile()
        .map { resource ->
            if (resource is Resource.Success) {
                resource.data.address
            } else {
                null
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _selectedMethod = MutableStateFlow(PaymentMethod.CASH)
    val selectedMethod: StateFlow<PaymentMethod> = _selectedMethod

    private val _appliedVoucher = MutableStateFlow<Voucher?>(null)

    private val _voucherMessage = MutableStateFlow<String?>(null)
    val voucherMessage = _voucherMessage.asStateFlow()

    val cartList: StateFlow<Resource<List<Cart>>> = cartUseCase.getCartItems()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Resource.Loading()
        )

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    val cartSummary: StateFlow<CartSummary> = combine(
        flow = cartList,
        flow2 = _appliedVoucher
    ) { cartResource, voucher ->
        if (cartResource is Resource.Success) {
            calculateSummaryService(cartResource.data, voucher)
        } else {
            CartSummary(0, 0, 0, 0)
        }
    }.stateIn(
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

    fun applyVoucher(code: String) {
        val cartResource = cartList.value

        if (cartResource !is Resource.Success) {
            _voucherMessage.value = "ERROR_CART_STILL_LOADING"
            return
        }

        val subtotal = cartResource.data.filter { it.isAvailable }
            .sumOf { it.product.price * it.quantity }
            .toDouble()

        viewModelScope.launch {
            val result = voucherUseCase.applyVoucher(code, subtotal)

            when(result) {
                is Resource.Success -> {
                    val voucher = result.data

                    _appliedVoucher.value = voucher
                    _voucherMessage.value = "SUCCESS_APPLIED_VOUCHER"
                }
                is Resource.Error -> {
                    _voucherMessage.value = result.message
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onOrderClicked() {
        if (_orderState.value is OrderState.Loading) return
        val currentAddress = userAddress.value
        if (currentAddress == null) {
            _errorState.value = "ERROR_NO_ADDRESS"
            return
        }

        val voucherCode = _appliedVoucher.value?.code
        viewModelScope.launch {
            _orderState.value = OrderState.Loading

            val result = orderUseCase.placeOrder(
                address = currentAddress,
                paymentMethod = _selectedMethod.value,
                voucher = voucherCode
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