package com.kevinfreyap.shared_cart.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CartProductResponseItem(

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
	val categoryResponse: CartCategoryResponse,

	@field:SerializedName("slug")
	val slug: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)