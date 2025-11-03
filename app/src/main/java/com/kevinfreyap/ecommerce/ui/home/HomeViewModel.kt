package com.kevinfreyap.ecommerce.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.Product
import com.kevinfreyap.core.domain.usecase.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productUseCase: ProductUseCase
) : ViewModel() {

    private val _productList = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val productList: StateFlow<Resource<List<Product>>> = _productList

    init {
        getProducts()
    }

    fun getProducts() {
        viewModelScope.launch {
            productUseCase.getProducts()
                .collect { products ->
                    _productList.value = products
                }
        }
    }
}