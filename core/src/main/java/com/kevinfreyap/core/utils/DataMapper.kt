package com.kevinfreyap.core.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kevinfreyap.core.data.source.local.entity.ProductEntity
import com.kevinfreyap.core.data.source.remote.response.CategoryResponse
import com.kevinfreyap.core.data.source.remote.response.ProductsResponseItem
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.model.product.ProductCategory

object DataMapper {

    private val gson = Gson()

    fun mapProductCategoryToDomain(categoryResponse: CategoryResponse): ProductCategory {
        return ProductCategory(
            id = categoryResponse.id,
            name = categoryResponse.name,
            image = categoryResponse.image
        )
    }

    fun mapProductResponseToEntity(response: ProductsResponseItem): ProductEntity {
        return ProductEntity(
            id = response.id,
            title = response.title,
            description = response.description,
            price = response.price,
            slug = response.slug,
            creationAt = response.creationAt,
            updatedAt = response.updatedAt,
            category = gson.toJson(response.categoryResponse),
            images = gson.toJson(response.images)
        )
    }

    fun mapProductsResponseToEntity(input: List<ProductsResponseItem>): List<ProductEntity> {
        return input.map { mapProductResponseToEntity(it) }
    }

    fun mapEntityToDomain(entity: ProductEntity): Product {
        val categoryObject = gson.fromJson(entity.category, ProductCategory::class.java)
        val imageList: List<String> = gson.fromJson(
            entity.images,
            object : TypeToken<List<String>>() {}.type
        )

        return Product(
            id = entity.id,
            title = entity.title,
            category = categoryObject,
            description = entity.description,
            price = entity.price,
            images = imageList,
            slug = entity.slug,
            creationAt = entity.creationAt,
            updatedAt = entity.updatedAt
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