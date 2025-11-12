package com.kevinfreyap.ecommerce.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.usecase.cart.CartUseCase
import com.kevinfreyap.core.domain.usecase.product.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cartUseCase: CartUseCase,
    productUseCase: ProductUseCase
) : ViewModel() {

    val productList: Flow<PagingData<Product>> = productUseCase.getProducts()
        .cachedIn(viewModelScope)

    val cartItemCount: StateFlow<Int> = cartUseCase.getCartItemCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
}