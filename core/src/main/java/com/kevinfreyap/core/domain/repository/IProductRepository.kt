package com.kevinfreyap.core.domain.repository

import androidx.paging.PagingData
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.filter.SearchFilter
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.model.product.ProductCategory
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    fun getProducts(query: String, filter: SearchFilter): Flow<PagingData<Product>>
    fun getProductById(productId: String): Flow<Resource<Product?>>
    suspend fun getProductByIdFromCache(productIds: List<String>): Flow<Resource<List<Product>>>
    fun getCategories(): Flow<Resource<List<ProductCategory>>>
    suspend fun refreshCategories()
}