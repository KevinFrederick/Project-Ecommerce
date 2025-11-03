package com.kevinfreyap.ecommerce.utils

import com.kevinfreyap.ecommerce.data.source.remote.response.CategoryResponse
import com.kevinfreyap.ecommerce.data.source.remote.response.ProductsResponseItem
import com.kevinfreyap.ecommerce.domain.model.Product
import com.kevinfreyap.ecommerce.domain.model.ProductCategory

object DataMapper {
    fun mapProductCategoryToDomain(categoryResponse: CategoryResponse): ProductCategory {
        return ProductCategory(
            id = categoryResponse.id,
            name = categoryResponse.name,
            image = categoryResponse.image
        )
    }

    fun mapProductResponseToDomain(response: ProductsResponseItem): Product {
        return Product(
            id = response.id,
            title = response.title,
            category = mapProductCategoryToDomain(response.categoryResponse),
            description = response.description,
            price = response.price,
            images = response.images,
            slug = response.slug,
            creationAt = response.creationAt,
            updatedAt = response.updatedAt
        )
    }

    fun mapProductsResponseToDomain(listResponse: List<ProductsResponseItem>): List<Product> {
        return listResponse.map { mapProductResponseToDomain(it) }
    }
}