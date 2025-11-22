package com.kevinfreyap.core.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val price: Int,
    val slug: String,
    val creationAt: Long,
    val updatedAt: Long,
    // We will store these complex types as a String (JSON)
    val category: String,
    val categoryName: String,
    val images: String
)
