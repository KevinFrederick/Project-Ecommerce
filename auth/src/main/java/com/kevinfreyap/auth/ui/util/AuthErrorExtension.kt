package com.kevinfreyap.auth.ui.util

import androidx.fragment.app.Fragment
import com.kevinfreyap.core.domain.validation.AuthErrorType
import com.kevinfreyap.shared_ui.R

fun Fragment.getErrorMessage(errorType: AuthErrorType): String {
    return when(errorType) {
        AuthErrorType.EMAIL_EMPTY -> getString(R.string.error_email_is_required)
        AuthErrorType.EMAIL_INVALID -> getString(R.string.error_enter_valid_email)
        AuthErrorType.PASSWORD_EMPTY -> getString(R.string.error_password_is_required)
        AuthErrorType.PASSWORD_TOO_SHORT -> getString(R.string.error_password_too_short)
        AuthErrorType.PASSWORD_DO_NOT_MATCH -> getString(R.string.error_password_do_not_match)
    }
}