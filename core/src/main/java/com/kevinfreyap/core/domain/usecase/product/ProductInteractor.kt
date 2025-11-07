package com.kevinfreyap.core.domain.usecase.product

import androidx.paging.PagingData
import com.kevinfreyap.core.data.repository.ProductRepository
import com.kevinfreyap.core.domain.model.product.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductInteractor @Inject constructor(private val productRepository: ProductRepository): ProductUseCase {
    override fun getProducts(): Flow<PagingData<Product>> = productRepository.getProducts()
}