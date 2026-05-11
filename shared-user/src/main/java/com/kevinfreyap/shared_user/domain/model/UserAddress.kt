package com.kevinfreyap.shared_user.domain.model

data class UserAddress(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val zipCode: String = ""
) {
    override fun toString(): String {
        return listOf(street, city, state, zipCode, country)
            .filter { it.isNotBlank() }
            .joinToString (", ")
    }
}
