package com.kevinfreyap.shared_product.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey
    val productId: Int,
    val nextOffset: Int?
)