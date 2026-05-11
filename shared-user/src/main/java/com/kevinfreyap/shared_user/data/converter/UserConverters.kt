package com.kevinfreyap.shared_user.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kevinfreyap.shared_user.domain.model.UserAddress

class UserConverters {
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
}