package com.kevinfreyap.shared_transaction.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kevinfreyap.shared_transaction.domain.model.TransactionItem

class TransactionTypeConverters {
    private val gson = Gson()

    // OrderItem List Converter
    @TypeConverter
    fun fromOrderItemList(items: List<TransactionItem>): String {
        return gson.toJson(items)
    }

    @TypeConverter
    fun toOrderItemList(json: String): List<TransactionItem> {
        val listType = object : TypeToken<List<TransactionItem>>() {}.type
        return gson.fromJson(json, listType)
    }
}