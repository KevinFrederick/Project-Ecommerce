package com.kevinfreyap.core.domain.usecase.product

import androidx.paging.PagingData
import com.kevinfreyap.core.domain.model.product.Product
import kotlinx.coroutines.flow.Flow

interface ProductUseCase {
    fun getProducts(): Flow<PagingData<Product>>
}