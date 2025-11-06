package com.kevinfreyap.core.domain.validation

sealed class ValidationResult {
    data object Success: ValidationResult()
    data class Error(val type: AuthErrorType): ValidationResult()
}