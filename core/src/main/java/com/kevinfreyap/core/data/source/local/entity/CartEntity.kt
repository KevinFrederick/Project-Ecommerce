package com.kevinfreyap.core.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_table")
data class CartEntity(
    @PrimaryKey val productId: String, // acts as ID
    val name: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int,
    val isAvailable: Boolean = true,
    val dateAdded: Long = System.currentTimeMillis()
)
