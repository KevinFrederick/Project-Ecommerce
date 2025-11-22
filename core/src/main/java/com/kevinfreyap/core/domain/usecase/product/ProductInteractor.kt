package com.kevinfreyap.core.domain.usecase.product

import androidx.paging.PagingData
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.filter.SearchFilter
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.model.product.ProductCategory
import com.kevinfreyap.core.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductInteractor @Inject constructor(
    private val productRepository: IProductRepository
): ProductUseCase {
    override fun getProducts(query: String, filter: SearchFilter): Flow<PagingData<Product>> {
        val cleanedQuery = query.trim()
        return productRepository.getProducts(cleanedQuery, filter)
    }
    override fun getProductById(productId: String): Flow<Resource<Product?>> = productRepository.getProductById(productId)
    override fun getCategories(): Flow<Resource<List<ProductCategory>>> = productRepository.getCategories()

    override suspend fun refreshCategories() {
        productRepository.refreshCategories()
    }
}