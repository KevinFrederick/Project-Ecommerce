package com.kevinfreyap.core.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kevinfreyap.core.utils.Constants.ROOM_TABLE_REMOTE_KEYS

@Entity(tableName = ROOM_TABLE_REMOTE_KEYS)
data class RemoteKeyEntity(
    @PrimaryKey
    val productId: Int,
    val nextOffset: Int?
)
