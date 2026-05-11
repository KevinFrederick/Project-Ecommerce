package com.kevinfreyap.shared_auth.domain.model

data class AuthSessionResult(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isGoogleAccount: Boolean
)
