package com.kevinfreyap.core.domain.model.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductCategory(
    val id: String,
    val name: String,
    val image: String,
): Parcelable
