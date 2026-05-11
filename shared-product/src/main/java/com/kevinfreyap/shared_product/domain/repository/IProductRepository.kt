package com.kevinfreyap.shared_product.domain.repository

import androidx.paging.PagingData
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_product.domain.model.SearchFilter
import com.kevinfreyap.shared_product.domain.model.Product
import com.kevinfreyap.shared_product.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    fun getProducts(query: String, filter: SearchFilter): Flow<PagingData<Product>>
    fun getProductById(productId: String): Flow<Resource<Product?>>
    suspend fun getProductByIdFromCache(productIds: List<String>): Flow<Resource<List<Product>>>
    fun getCategories(): Flow<Resource<List<ProductCategory>>>
    suspend fun refreshCategories()
}