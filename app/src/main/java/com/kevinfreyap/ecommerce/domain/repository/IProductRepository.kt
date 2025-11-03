package com.kevinfreyap.ecommerce.domain.repository

import com.kevinfreyap.ecommerce.data.Resource
import com.kevinfreyap.ecommerce.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    fun getProducts(): Flow<Resource<List<Product>>>
}