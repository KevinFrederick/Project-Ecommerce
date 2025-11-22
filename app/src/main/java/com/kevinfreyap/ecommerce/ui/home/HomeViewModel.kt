package com.kevinfreyap.ecommerce.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kevinfreyap.core.domain.model.filter.SearchFilter
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.usecase.product.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    productUseCase: ProductUseCase
) : ViewModel() {

    val productList: Flow<PagingData<Product>> = productUseCase.getProducts(
        query = "",
        filter = SearchFilter()
    )
        .cachedIn(viewModelScope)
}