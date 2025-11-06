package com.kevinfreyap.core.domain.validation

object AuthValidator {
    fun validateEmail(email: String): ValidationResult {
        if (email.isEmpty()) {
            return ValidationResult.Error(AuthErrorType.EMAIL_EMPTY)
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult.Error(AuthErrorType.EMAIL_INVALID)
        }
        return ValidationResult.Success
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult.Error(AuthErrorType.PASSWORD_EMPTY)
        }
        if (password.length < 8) {
            return ValidationResult.Error(AuthErrorType.PASSWORD_TOO_SHORT)
        }
        return ValidationResult.Success
    }
}