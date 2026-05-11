package com.kevinfreyap.shared_user.domain.model

data class UserProfile(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val address: UserAddress? = null,
    val isGoogleAccount: Boolean = false
)