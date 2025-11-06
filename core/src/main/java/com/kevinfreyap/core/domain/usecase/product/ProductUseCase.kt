package com.kevinfreyap.core.domain.usecase.product

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import kotlinx.coroutines.flow.Flow

interface ProductUseCase {
    fun getProducts(): Flow<Resource<List<Product>>>
}