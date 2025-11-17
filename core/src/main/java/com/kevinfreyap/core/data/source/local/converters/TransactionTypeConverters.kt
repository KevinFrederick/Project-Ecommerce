package com.kevinfreyap.core.data.source.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kevinfreyap.core.domain.model.order.OrderItem
import com.kevinfreyap.core.domain.model.user.UserAddress

class TransactionTypeConverters {
    private val gson = Gson()

    // UserAddress Converter
    @TypeConverter
    fun fromAddress(address: UserAddress): String {
        return gson.toJson(address)
    }

    @TypeConverter
    fun toAddress(json: String): UserAddress {
        return gson.fromJson(json, UserAddress::class.java)
    }

    // OrderItem List Converter
    @TypeConverter
    fun fromOrderItemList(items: List<OrderItem>): String {
        return gson.toJson(items)
    }

    @TypeConverter
    fun toOrderItemList(json: String): List<OrderItem> {
        val listType = object : TypeToken<List<OrderItem>>() {}.type
        return gson.fromJson(json, listType)
    }
}