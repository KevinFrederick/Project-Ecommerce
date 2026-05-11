package com.kevinfreyap.shared_product.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CategoryResponse(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("creationAt")
	val creationAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("slug")
	val slug: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)