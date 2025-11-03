package com.kevinfreyap.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ProductsResponseItem(

	@field:SerializedName("images")
	val images: List<String>,

	@field:SerializedName("creationAt")
	val creationAt: String,

	@field:SerializedName("price")
	val price: Int,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("category")
	val categoryResponse: CategoryResponse,

	@field:SerializedName("slug")
	val slug: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)