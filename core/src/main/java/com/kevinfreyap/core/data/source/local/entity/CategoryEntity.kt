package com.kevinfreyap.core.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val slug: String,
    val image: String,
    val creationAt: Long,
    val updateAt: Long
)
