package com.kevinfreyap.wishlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_wishlist.domain.model.WishlistItem
import com.kevinfreyap.shared_wishlist.domain.usecase.GetWishlistUseCase
import com.kevinfreyap.shared_wishlist.domain.usecase.RemoveFromWishlistUseCase
import com.kevinfreyap.shared_wishlist.domain.usecase.ValidateWishlistAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    getWishlist: GetWishlistUseCase,
    private val validateWishlistAvailability: ValidateWishlistAvailabilityUseCase,
    private val removeFromWishlist: RemoveFromWishlistUseCase,
): ViewModel() {
    val wishlist: StateFlow<Resource<List<WishlistItem>>> = getWishlist()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    init {
        refreshAvailability()
    }

    fun refreshAvailability() {
        viewModelScope.launch {
            validateWishlistAvailability()
        }
    }

    fun onRemoveWishlist(productId: String) {
        viewModelScope.launch {
            removeFromWishlist(productId)
        }
    }
}