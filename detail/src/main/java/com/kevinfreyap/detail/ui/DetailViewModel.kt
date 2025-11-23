package com.kevinfreyap.detail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.usecase.cart.CartUseCase
import com.kevinfreyap.core.domain.usecase.product.ProductUseCase
import com.kevinfreyap.core.domain.usecase.wishlist.WishlistUseCase
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
    private val wishlistUseCase: WishlistUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val productId = savedStateHandle.get<String>("productId")

    private val _productState = MutableStateFlow<Resource<Product?>>(Resource.Loading())
    val productState : StateFlow<Resource<Product?>> = _productState

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    private val _addToCartState = MutableStateFlow<Resource<Boolean>?>(null)
    val addToCartState: StateFlow<Resource<Boolean>?> = _addToCartState

    private val _wishlistState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val wishlistState: StateFlow<Resource<Unit>> = _wishlistState
    val isInWishlist: StateFlow<Boolean> = wishlistUseCase.observeIsProductInWishlist(productId ?: "")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        if (productId != null) {
            loadProduct(productId)
        } else {
            _productState.value = Resource.Error("ERROR_PRODUCT_UNAVAILABLE")
        }
    }

    private fun loadProduct(id: String) {
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

    fun onClickWishlist() {
        if (isInWishlist.value) {
            onRemoveWishlist()
        } else {
            addToWishlist()
        }
    }

    fun addToWishlist() {
        viewModelScope.launch {
            if (productId != null){
                wishlistUseCase.addToWishlist(productId).collect { resource ->
                    _wishlistState.value = resource
                }
            }
        }
    }

    fun onRemoveWishlist() {
        viewModelScope.launch {
            if (productId != null) {
                wishlistUseCase.removeFromWishlist(productId)
            }
        }
    }

    fun onAddToCartClicked(product: Product) {
        val currentQuantity = quantity.value

        if (currentQuantity > 0) {
            viewModelScope.launch{
                cartUseCase.addToCart(product, currentQuantity).collect { resource ->
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