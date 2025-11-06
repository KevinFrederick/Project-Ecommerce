package com.kevinfreyap.core.domain.validation

enum class AuthErrorType {
    EMAIL_EMPTY,
    EMAIL_INVALID,
    PASSWORD_EMPTY,
    PASSWORD_TOO_SHORT,
    PASSWORD_DO_NOT_MATCH
}