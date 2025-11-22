package com.kevinfreyap.core.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kevinfreyap.core.data.source.local.entity.CartEntity
import com.kevinfreyap.core.data.source.local.entity.CategoryEntity
import com.kevinfreyap.core.data.source.local.entity.ProductEntity
import com.kevinfreyap.core.data.source.local.entity.TransactionEntity
import com.kevinfreyap.core.data.source.local.entity.WishlistEntity
import com.kevinfreyap.core.data.source.remote.response.CategoryResponse
import com.kevinfreyap.core.data.source.remote.response.ProductsResponseItem
import com.kevinfreyap.core.domain.model.order.OrderItem
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.model.product.ProductCategory
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.wishlist.WishlistItem

object DataMapper {

    private val gson = Gson()

    // Product
    fun mapProductCategoryToDomain(categoryResponse: CategoryResponse): ProductCategory {
        return ProductCategory(
            id = categoryResponse.id.toString(),
            name = categoryResponse.name,
            image = categoryResponse.image
        )
    }

    fun mapProductResponseToEntity(response: ProductsResponseItem): ProductEntity {
        return ProductEntity(
            id = response.id.toString(),
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
            id = response.id.toString(),
            title = response.title,
            category = mapProductCategoryToDomain(response.categoryResponse),
            description = response.description,
            price = response.price,
            images = response.images,
            slug = response.slug,
            creationAt = DateHelper.parseIsoStringToLong(response.creationAt),
            updatedAt = DateHelper.parseIsoStringToLong(response.updatedAt)
        )
    }


    // Cart
    fun mapCartEntityToDomain(entity: CartEntity): Product {
        return Product(
            id = entity.productId,
            title = entity.name,
            price = entity.price,
            images = listOf(entity.imageUrl),
            category = ProductCategory("", "", ""),
            description = "",
            slug = "",
            creationAt = 0L,
            updatedAt = 0L
        )
    }

    // Order / Transaction
    fun mapOrderDomainToEntity(domain: OrderReceipt): TransactionEntity {
        return TransactionEntity(
            transactionId = domain.orderId,
            datePlaced = domain.datePlaced,
            totalPaid = domain.totalPaid,
            subtotal = domain.subtotal,
            shippingFee = domain.shippingFee,
            discountAmount = domain.discountAmount,
            orderStatus = domain.orderStatus,
            shippingAddressJson = gson.toJson(domain.shippingAddress),
            itemsPurchasedJson = gson.toJson(domain.itemsPurchased),
            paymentMethod = domain.paymentMethod
        )
    }

    fun mapTransactionEntityToDomain(entity: TransactionEntity): OrderReceipt {
        val itemsPurchasedType = object : TypeToken<List<OrderItem>>() {}.type

        return OrderReceipt(
            orderId = entity.transactionId,
            datePlaced = entity.datePlaced,
            totalPaid = entity.totalPaid,
            subtotal = entity.subtotal,
            shippingFee = entity.shippingFee,
            discountAmount = entity.discountAmount,
            orderStatus = entity.orderStatus,
            shippingAddress = gson.fromJson(entity.shippingAddressJson, UserAddress::class.java),
            itemsPurchased = gson.fromJson(entity.itemsPurchasedJson, itemsPurchasedType),
            paymentMethod = entity.paymentMethod
        )
    }

    fun mapTransactionsEntityToDomain(entities: List<TransactionEntity>): List<OrderReceipt> {
        return entities.map { mapTransactionEntityToDomain(it) }
    }


    // Wishlist
    fun mapWishlistEntityToDomain(entity: WishlistEntity): WishlistItem {
        return WishlistItem(
            productId = entity.productId,
            dateAdded = entity.dateAdded
        )
    }

    fun mapWishlistsToDomain(entities: List<WishlistEntity>): List<WishlistItem> {
        return entities.map {
            mapWishlistEntityToDomain(it)
        }
    }

    fun mapWishlistDomainToEntity(domain: WishlistItem): WishlistEntity {
        return WishlistEntity(
            productId = domain.productId,
            dateAdded = domain.dateAdded
        )
    }

    fun mapWishlistsDomainToEntity(domains: List<WishlistItem>): List<WishlistEntity> {
        return domains.map { mapWishlistDomainToEntity(it) }
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