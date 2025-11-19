package com.kevinfreyap.wishlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.usecase.wishlist.WishlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
   private val wishlistUseCase: WishlistUseCase
): ViewModel() {
    val wishlist: StateFlow<Resource<List<Product>>> = wishlistUseCase.getWishlist()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    fun onRemoveWishlist(productId: String) {
        viewModelScope.launch {
            wishlistUseCase.removeFromWishlist(productId)
        }
    }
}