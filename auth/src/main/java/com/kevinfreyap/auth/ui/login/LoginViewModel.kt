package com.kevinfreyap.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.shared_auth.domain.model.AuthRequest
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.validation.AuthErrorType
import com.kevinfreyap.core.domain.validation.AuthValidator
import com.kevinfreyap.core.domain.validation.ValidationResult
import com.kevinfreyap.auth.domain.usecase.LoginUseCase
import com.kevinfreyap.auth.domain.usecase.LoginWithGoogleUseCase
import com.kevinfreyap.shared_auth.domain.usecase.SendResetPassEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val sendResetPassEmailUseCase: SendResetPassEmailUseCase
): ViewModel() {
    private val _loginState = MutableStateFlow<Resource<Boolean>?>(null)
    val loginState: StateFlow<Resource<Boolean>?> = _loginState

    private val _resetPasswordState = MutableStateFlow<Resource<Unit>?>(null)
    val resetPasswordState: StateFlow<Resource<Unit>?> = _resetPasswordState

    private val _emailError = MutableStateFlow<AuthErrorType?>(null)
    val emailError : StateFlow<AuthErrorType?> = _emailError

    private val _passError = MutableStateFlow<AuthErrorType?>(null)
    val passError: StateFlow<AuthErrorType?> = _passError

    fun login(email: String, pass: String) {
        val emailValidation = AuthValidator.validateEmail(email)
        if (emailValidation is ValidationResult.Error) {
            _emailError.value = emailValidation.type
        } else {
            _emailError.value = null
        }

        val passValidation = AuthValidator.validatePassword(pass)
        if (passValidation is ValidationResult.Error) {
            _passError.value = passValidation.type
        } else {
            _passError.value = null
        }

        if (_emailError.value != null || _passError.value != null) {
            return
        }

        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val loginRequest = AuthRequest(email, pass)

            val result = loginUseCase(loginRequest)
            _loginState.value = result
        }
    }

    fun onGoogleIdTokenReceived(idToken: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val result = loginWithGoogleUseCase(idToken)
            _loginState.value = result
        }
    }

    fun onForgotPasswordClicked(email: String) {
        viewModelScope.launch {
            val result = sendResetPassEmailUseCase(email)
            _resetPasswordState.value = result
        }
    }

    fun clearEmailError() {
        if (_emailError.value != null){
            _emailError.value = null
        }
    }

    fun clearPassError() {
        if (_passError.value != null) {
            _passError.value = null
        }
    }

    fun resetForgotPasswordState() {
        _resetPasswordState.value = null
    }
}