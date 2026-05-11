package com.kevinfreyap.shared_product.domain.usecase

import androidx.paging.PagingData
import com.kevinfreyap.shared_product.domain.model.Product
import com.kevinfreyap.shared_product.domain.model.SearchFilter
import com.kevinfreyap.shared_product.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductUseCase @Inject constructor(
    private val productRepository: IProductRepository
) {
    operator fun invoke(query: String, filter: SearchFilter): Flow<PagingData<Product>> {
        val cleanedQuery = query.trim()
        return productRepository.getProducts(cleanedQuery, filter)
    }
}