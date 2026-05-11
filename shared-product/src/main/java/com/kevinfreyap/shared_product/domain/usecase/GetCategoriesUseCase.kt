package com.kevinfreyap.shared_product.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_product.domain.model.ProductCategory
import com.kevinfreyap.shared_product.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val productRepository: IProductRepository
) {
    operator fun invoke (): Flow<Resource<List<ProductCategory>>> = productRepository.getCategories()
}