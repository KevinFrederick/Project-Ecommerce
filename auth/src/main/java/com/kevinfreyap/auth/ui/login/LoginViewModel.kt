package com.kevinfreyap.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.usecase.auth.AuthUseCase
import com.kevinfreyap.core.domain.usecase.cart.CartUseCase
import com.kevinfreyap.core.domain.validation.AuthErrorType
import com.kevinfreyap.core.domain.validation.AuthValidator
import com.kevinfreyap.core.domain.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val cartUseCase: CartUseCase
): ViewModel() {
    private val _loginState = MutableStateFlow<Resource<Boolean>?>(null)
    val loginState: StateFlow<Resource<Boolean>?> = _loginState

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
            val loginRequest = LoginRequest(email, pass)
            authUseCase.login(loginRequest).collect { value ->
                if (value is Resource.Success) {
                    cartUseCase.syncCartOnLogin()
                }
                _loginState.value = value
            }
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
}