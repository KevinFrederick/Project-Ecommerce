package com.kevinfreyap.core.domain.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserAddress(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val zipCode: String = ""
): Parcelable {
    override fun toString(): String {
        return listOf(street, city, state, zipCode, country)
            .filter { it.isNotBlank() }
            .joinToString (", ")
    }
}
