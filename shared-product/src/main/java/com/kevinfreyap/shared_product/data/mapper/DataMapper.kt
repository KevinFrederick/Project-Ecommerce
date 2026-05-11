package com.kevinfreyap.shared_product.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kevinfreyap.shared_product.data.source.local.entity.CategoryEntity
import com.kevinfreyap.shared_product.data.source.local.entity.ProductEntity
import com.kevinfreyap.core.utils.DateHelper
import com.kevinfreyap.shared_product.data.source.remote.response.CategoryResponse
import com.kevinfreyap.shared_product.data.source.remote.response.ProductsResponseItem
import com.kevinfreyap.shared_product.domain.model.Product
import com.kevinfreyap.shared_product.domain.model.ProductCategory

object DataMapper {

    private val gson = Gson()

    // Product
    fun mapProductResponseToEntity(response: ProductsResponseItem): ProductEntity {
        return ProductEntity(
            id = response.id,
            title = response.title,
            description = response.description,
            price = response.price,
            slug = response.slug,
            creationAt = DateHelper.parseIsoStringToLong(response.creationAt),
            updatedAt = DateHelper.parseIsoStringToLong(response.updatedAt),
            category = gson.toJson(response.categoryResponse),
            categoryName = response.categoryResponse.name,
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
            id = entity.id.toString(),
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


    // Category
    fun mapCategoryEntityToDomain(entity: CategoryEntity): ProductCategory {
        return ProductCategory(
            id = entity.id,
            name = entity.name,
            image = entity.image
        )
    }

    fun mapCategoryResponseToEntity(response: CategoryResponse): CategoryEntity {
        return CategoryEntity(
            id = response.id.toString(),
            name = response.name,
            slug = response.slug,
            image = response.image,
            creationAt = DateHelper.parseIsoStringToLong(response.creationAt),
            updateAt = DateHelper.parseIsoStringToLong(response.updatedAt)
        )
    }
}