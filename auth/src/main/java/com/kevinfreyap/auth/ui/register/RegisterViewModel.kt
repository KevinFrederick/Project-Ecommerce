package com.kevinfreyap.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.shared_auth.domain.model.AuthRequest
import com.kevinfreyap.auth.ui.nav.AuthNav
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_auth.domain.usecase.AuthUseCase
import com.kevinfreyap.core.domain.validation.AuthErrorType
import com.kevinfreyap.core.domain.validation.AuthValidator
import com.kevinfreyap.core.domain.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
): ViewModel() {
    private val _registerState = MutableStateFlow<Resource<Boolean>?>(null)
    val registerState: StateFlow<Resource<Boolean>?> = _registerState

    private val _navEvent = Channel<AuthNav>()
    val navEvent= _navEvent.receiveAsFlow()

    private val _emailError = MutableStateFlow<AuthErrorType?>(null)
    val emailError: StateFlow<AuthErrorType?> = _emailError

    private val _passError = MutableStateFlow<AuthErrorType?>(null)
    val passError: StateFlow<AuthErrorType?> = _passError

    private val _confirmPassError = MutableStateFlow<AuthErrorType?>(null)
    val confirmPassError: StateFlow<AuthErrorType?> = _confirmPassError

    fun register(email: String, pass: String, confirmPass: String) {
        val emailValidation = AuthValidator.validateEmail(email)
        if (emailValidation is ValidationResult.Error) {
            _emailError.value = emailValidation.type
        } else {
            _emailError.value = null
        }

        val passwordValidation = AuthValidator.validatePassword(pass)
        if (passwordValidation is ValidationResult.Error) {
            _passError.value = passwordValidation.type
        } else {
            _passError.value = null
        }

        if (pass.isNotEmpty() && pass != confirmPass) {
            _confirmPassError.value = AuthErrorType.PASSWORD_DO_NOT_MATCH
        } else {
            _confirmPassError.value = null
        }

        if (_emailError.value != null || _passError.value != null || _confirmPassError.value != null) {
            return
        }

        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            val request = AuthRequest(email, pass)
            authUseCase.register(request).collect { value ->
                _registerState.value = value
                if (value is Resource.Success){
                    _navEvent.send(AuthNav.ToLogin)
                }
            }
        }
    }

    fun onGoogleIdTokenReceived(idToken: String) {
        viewModelScope.launch {
            authUseCase.loginWithGoogle(idToken).collect { resource ->
                _registerState.value = resource

                if (resource is Resource.Success) {
                    _navEvent.send(AuthNav.ToAccount)
                }
            }
        }
    }

    fun clearEmailError() {
        if (_emailError.value != null) {
            _emailError.value = null
        }
    }

    fun clearPasswordError() {
        if (_passError.value != null) {
            _passError.value = null
        }
    }

    fun clearConfirmPasswordError() {
        if (_confirmPassError.value != null) {
            _confirmPassError.value = null
        }
    }
}