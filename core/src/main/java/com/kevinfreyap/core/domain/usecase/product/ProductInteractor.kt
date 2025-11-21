package com.kevinfreyap.core.domain.usecase.product

import androidx.paging.PagingData
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductInteractor @Inject constructor(
    private val productRepository: IProductRepository
): ProductUseCase {
    override fun getProducts(query: String): Flow<PagingData<Product>> {
        val cleanedQuery = query.trim()
        return productRepository.getProducts(cleanedQuery)
    }
    override fun getProductById(productId: String): Flow<Resource<Product?>> = productRepository.getProductById(productId)
}