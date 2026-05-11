package com.kevinfreyap.shared_product.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kevinfreyap.shared_product.domain.model.ProductCategory

class ProductConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromCategory(category: ProductCategory): String {
        return gson.toJson(category)
    }

    @TypeConverter
    fun toCategory(categoryString: String): ProductCategory {
        return gson.fromJson(categoryString, ProductCategory::class.java)
    }

    @TypeConverter
    fun fromImagesList(images: List<String>): String {
        return gson.toJson(images)
    }

    @TypeConverter
    fun toImagesList(imagesString: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(imagesString, listType)
    }
}