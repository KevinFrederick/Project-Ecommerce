package com.kevinfreyap.core.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WishlistEntity(
    @PrimaryKey
    val productId: String,
    val dateAdded: Long
)
