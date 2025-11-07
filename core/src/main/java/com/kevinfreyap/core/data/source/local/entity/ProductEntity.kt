package com.kevinfreyap.core.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kevinfreyap.core.utils.Constants.ROOM_TABLE_PRODUCT

@Entity(tableName = ROOM_TABLE_PRODUCT)
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val price: Int,
    val slug: String,
    val creationAt: String,
    val updatedAt: String,
    // We will store these complex types as a String (JSON)
    val category: String,
    val images: String
)
