package com.kevinfreyap.product.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kevinfreyap.core.domain.network.INetworkMonitor
import com.kevinfreyap.shared_cart.domain.usecase.GetCartItemCountUseCase
import com.kevinfreyap.shared_product.domain.model.Product
import com.kevinfreyap.shared_product.domain.model.SearchFilter
import com.kevinfreyap.shared_product.domain.usecase.GetProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getProductUseCase: GetProductUseCase,
    getCartItemCount: GetCartItemCountUseCase,
    networkMonitor: INetworkMonitor,
) : ViewModel() {
    val productList: Flow<PagingData<Product>> = getProductUseCase(
        query = "",
        filter = SearchFilter()
    )
        .cachedIn(viewModelScope)

    val cartItemCount: StateFlow<Int> = getCartItemCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    val isNetworkAvailable: StateFlow<Boolean> = networkMonitor.isInternetAvailableFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
}