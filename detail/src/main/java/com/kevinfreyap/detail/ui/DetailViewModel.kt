package com.kevinfreyap.detail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.usecase.product.ProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val productId = savedStateHandle.get<Int>("productId")

    private val _productState = MutableStateFlow<Resource<Product?>>(Resource.Loading())
    val productState : StateFlow<Resource<Product?>> = _productState

    init {
        if (productId != null) {
            loadProduct(productId)
        } else {
            _productState.value = Resource.Error("Product ID is missing")
        }
    }

    private fun loadProduct(id: Int) {
        viewModelScope.launch {
            productUseCase.getProductById(id).collect {
                _productState.value = it
            }
        }
    }
}