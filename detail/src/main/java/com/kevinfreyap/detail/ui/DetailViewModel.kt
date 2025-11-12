package com.kevinfreyap.detail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.usecase.cart.CartUseCase
import com.kevinfreyap.core.domain.usecase.product.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    private val cartUseCase: CartUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val productId = savedStateHandle.get<Int>("productId")

    private val _productState = MutableStateFlow<Resource<Product?>>(Resource.Loading())
    val productState : StateFlow<Resource<Product?>> = _productState

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    private val _addToCartState = MutableStateFlow<Resource<Boolean>?>(null)
    val addToCartState: StateFlow<Resource<Boolean>?> = _addToCartState

    val cartItemCount: StateFlow<Int> = cartUseCase.getCartItemCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    init {
        if (productId != null) {
            loadProduct(productId)
        } else {
            _productState.value = Resource.Error("ERROR_PRODUCT_UNAVAILABLE")
        }
    }

    private fun loadProduct(id: Int) {
        viewModelScope.launch {
            productUseCase.getProductById(id).collect {
                _productState.value = it
            }
        }
    }

    fun increaseQuantity() {
        _quantity.value++
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value--
        }
    }

    fun onAddToCartClicked() {
        val currentProduct = (productState.value as? Resource.Success)?.data
        val currentQuantity = quantity.value

        if (currentProduct != null && currentQuantity > 0) {
            viewModelScope.launch{
                cartUseCase.addToCart(currentProduct.id, currentQuantity).collect { resource ->
                    _addToCartState.value = resource
                }
            }
        } else {
            _addToCartState.value = Resource.Error("ERROR_PRODUCT_UNAVAILABLE")
        }
    }

    fun clearAddToCartState(){
        _addToCartState.value = null
    }

}