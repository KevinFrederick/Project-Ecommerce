package com.kevinfreyap.core.data.source.local.converters

import androidx.room.TypeConverter
import com.kevinfreyap.core.domain.model.order.OrderStatus

class OrderTypeConverters {
    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String {
        return status.name
    }

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus {
        return OrderStatus.fromString(value)
    }
}