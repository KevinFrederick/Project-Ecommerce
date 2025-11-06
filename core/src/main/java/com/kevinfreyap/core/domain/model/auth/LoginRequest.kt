package com.kevinfreyap.core.domain.model.auth

data class LoginRequest(
    val email: String,
    val password: String
)
