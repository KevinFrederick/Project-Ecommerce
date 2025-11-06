package com.kevinfreyap.core.domain.model.auth

data class UserProfile(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String? = null
)
