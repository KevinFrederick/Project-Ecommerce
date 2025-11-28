package com.kevinfreyap.core.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WishlistEntity(
    @PrimaryKey
    val productId: String,
    val dateAdded: Long,
    val productCategory: String,
    val productName: String,
    val productPrice: Int,
    val productImage: String,
    val isAvailable: Boolean = true
)
