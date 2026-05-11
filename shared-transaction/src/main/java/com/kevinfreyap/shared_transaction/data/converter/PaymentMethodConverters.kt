package com.kevinfreyap.shared_transaction.data.converter

import androidx.room.TypeConverter
import com.kevinfreyap.shared_transaction.domain.model.PaymentMethod

class PaymentMethodConverters {
    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod {
        return PaymentMethod.valueOf(value)
    }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod): String {
        return value.name
    }
}