package com.kevinfreyap.core.domain.model.auth

data class RegisterRequest(
    val email: String,
    val password: String
)
