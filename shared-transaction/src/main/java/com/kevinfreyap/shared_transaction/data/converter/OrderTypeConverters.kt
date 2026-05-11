package com.kevinfreyap.shared_transaction.data.converter

import androidx.room.TypeConverter
import com.kevinfreyap.shared_transaction.domain.model.TransactionStatus

class OrderTypeConverters {
    @TypeConverter
    fun fromOrderStatus(status: TransactionStatus): String {
        return status.name
    }

    @TypeConverter
    fun toOrderStatus(value: String): TransactionStatus {
        return TransactionStatus.fromString(value)
    }
}