package com.kevinfreyap.product.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.product.model.ProductUi
import com.kevinfreyap.shared_cart.domain.usecase.AddToCartUseCase
import com.kevinfreyap.shared_cart.domain.usecase.GetCartItemCountUseCase
import com.kevinfreyap.shared_product.domain.model.Product
import com.kevinfreyap.shared_product.domain.usecase.GetProductByIdUseCase
import com.kevinfreyap.shared_wishlist.domain.usecase.AddToWishlistUseCase
import com.kevinfreyap.shared_wishlist.domain.usecase.ObserveIsProductInWishlistUseCase
import com.kevinfreyap.shared_wishlist.domain.usecase.RemoveFromWishlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    observeIsProductInWishlist: ObserveIsProductInWishlistUseCase,
    getCartItemCount: GetCartItemCountUseCase,
    private val getProductById: GetProductByIdUseCase,
    private val addToWishlistUseCase: AddToWishlistUseCase,
    private val removeFromWishlist: RemoveFromWishlistUseCase,
    private val addToCart: AddToCartUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val cartItemCount: StateFlow<Int> = getCartItemCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )
    private val productId = savedStateHandle.get<String>("productId")

    private val _productState = MutableStateFlow<Resource<Product?>>(Resource.Loading())
    val productState : StateFlow<Resource<Product?>> = _productState

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    private val _addToCartState = MutableStateFlow<Resource<Boolean>?>(null)
    val addToCartState: StateFlow<Resource<Boolean>?> = _addToCartState

    private val _wishlistState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val wishlistState: StateFlow<Resource<Unit>> = _wishlistState
    val isInWishlist: StateFlow<Boolean> = observeIsProductInWishlist(productId ?: "")
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
            getProductById(id).collect {
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
            if (_productState.value is Resource.Success){
                val product = _productState.value.data
                if (product != null){
                    with(product) {
                        addToWishlistUseCase(
                            id = id,
                            title = title,
                            price = price,
                            categoryId = category.id,
                            categoryName = category.name,
                            categoryImage = category.image,
                            images = images,
                        ).collect { resource ->
                            _wishlistState.value = resource
                        }
                    }
                }
            }
        }
    }

    fun onRemoveWishlist() {
        viewModelScope.launch {
            if (productId != null) {
                removeFromWishlist(productId)
            }
        }
    }

    fun onAddToCartClicked(product: ProductUi) {
        val currentQuantity = quantity.value

        if (currentQuantity > 0) {
            viewModelScope.launch{
                with(product) {
                    addToCart(
                        productId = id,
                        productTitle = name,
                        productPrice = price,
                        productImages = imageUrls,
                        quantity = currentQuantity
                    ).collect { resource ->
                        _addToCartState.value = resource
                    }
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