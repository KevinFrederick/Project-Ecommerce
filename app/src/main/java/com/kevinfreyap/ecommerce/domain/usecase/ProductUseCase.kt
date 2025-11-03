package com.kevinfreyap.ecommerce.domain.usecase

import com.kevinfreyap.ecommerce.data.Resource
import com.kevinfreyap.ecommerce.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductUseCase {
    fun getProducts(): Flow<Resource<List<Product>>>
}