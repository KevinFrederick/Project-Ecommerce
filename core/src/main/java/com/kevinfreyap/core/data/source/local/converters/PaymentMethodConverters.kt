package com.kevinfreyap.core.data.source.local.converters

import androidx.room.TypeConverter
import com.kevinfreyap.core.utils.PaymentMethod

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