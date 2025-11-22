package com.kevinfreyap.core.domain.usecase.product

import androidx.paging.PagingData
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.filter.SearchFilter
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.model.product.ProductCategory
import kotlinx.coroutines.flow.Flow

interface ProductUseCase {
    fun getProducts(query: String, filter: SearchFilter): Flow<PagingData<Product>>
    fun getProductById(productId: String): Flow<Resource<Product?>>
    fun getCategories(): Flow<Resource<List<ProductCategory>>>
    suspend fun refreshCategories()
}