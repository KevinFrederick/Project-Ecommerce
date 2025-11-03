package com.kevinfreyap.core.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductUseCase {
    fun getProducts(): Flow<Resource<List<Product>>>
}