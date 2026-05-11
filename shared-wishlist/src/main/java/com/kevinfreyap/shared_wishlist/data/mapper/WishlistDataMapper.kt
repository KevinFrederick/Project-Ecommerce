package com.kevinfreyap.shared_wishlist.data.mapper

import com.kevinfreyap.shared_wishlist.data.source.local.entity.WishlistEntity
import com.kevinfreyap.shared_wishlist.domain.model.FirestoreWishlistItem
import com.kevinfreyap.shared_wishlist.domain.model.WishlistItem

object WishlistDataMapper {
    // Wishlist
    fun mapWishlistEntityToDomain(entity: WishlistEntity): WishlistItem {
        return WishlistItem(
            productId = entity.productId,
            dateAdded = entity.dateAdded,
            productCategory = entity.productCategory,
            productName = entity.productName,
            productPrice = entity.productPrice,
            productImage = entity.productImage,
            isAvailable = entity.isAvailable,
        )
    }

    fun mapWishlistsToDomain(entities: List<WishlistEntity>): List<WishlistItem> {
        return entities.map {
            mapWishlistEntityToDomain(it)
        }
    }

    fun mapFirestoreWishlistToEntity(firestore: FirestoreWishlistItem): WishlistEntity {
        return WishlistEntity(
            productId = firestore.productId,
            dateAdded = firestore.dateAdded,
            productName = firestore.productName,
            productPrice = firestore.productPrice,
            productImage = firestore.productImage,
            productCategory = firestore.productCategory,
            isAvailable = true,
        )
    }

    fun mapFirestoreWishlistsToEntity(domains: List<FirestoreWishlistItem>): List<WishlistEntity> {
        return domains.map { mapFirestoreWishlistToEntity(it) }
    }
}