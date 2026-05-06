package com.kevinfreyap.settings.ui

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.notification.NotificationPreferences
import com.kevinfreyap.core.domain.model.user.UserProfile
import com.kevinfreyap.core.domain.usecase.user.UserUseCase
import com.kevinfreyap.shared_auth.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase
): ViewModel(){

    val currentUser: StateFlow<Resource<UserProfile>> = userUseCase.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    val currentTheme: StateFlow<Int> = userUseCase.getTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

    val notificationSettings = userUseCase.getNotificationSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotificationPreferences()
        )

    private val _navEvent = Channel<Boolean>()
    val navEvent = _navEvent.receiveAsFlow()

    private val _updateState = MutableStateFlow<Resource<Unit>?>(null)
    val updateState: StateFlow<Resource<Unit>?> = _updateState

    private val _deleteState = MutableStateFlow<Resource<Unit>?>(null)
    val deleteState: StateFlow<Resource<Unit>?> = _deleteState

    fun setTheme(mode: Int) {
        viewModelScope.launch {
            userUseCase.saveTheme(mode)
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    fun updateNotificationPreferences(isSystem: Boolean, isEnabled: Boolean) {
        viewModelScope.launch {
            userUseCase.updateNotificationSetting(isSystem, isEnabled)
        }
    }

    fun onChangePassword(currentPass: String, newPass: String, confirmPass: String) {
        viewModelScope.launch {
            _updateState.value = Resource.Loading()

            val result = authUseCase.updatePassword(
                currentPass = currentPass,
                newPass = newPass,
                confirmPass = confirmPass
            )

            _updateState.value = result
        }
    }

    fun resetUpdateState() {
        _updateState.value = null
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authUseCase.logout()

                // Will execute only after authUseCase.logout Finished
                _navEvent.send(true)
            } catch (e: IOException) {
                Log.e("AccountViewModel", "Failed to clear auth token", e)
            } catch (e: Exception) {
                Log.e("AccountViewModel", "Logout Failed", e)
            }
        }
    }

    fun onDeleteAccountWithPassword(password: String) {
        viewModelScope.launch {
            _deleteState.value = Resource.Loading()
            val result = authUseCase.reAuthAndDeleteWithPassword(password)
            _deleteState.value = result
            if (result is Resource.Success) {
                _navEvent.send(true)
            }
        }
    }

    fun onDeleteAccountWithGoogle(idToken: String) {
        viewModelScope.launch {
            _deleteState.value = Resource.Loading()
            val result = authUseCase.reAuthAndDeleteWithGoogle(idToken)
            _deleteState.value = result
            if (result is Resource.Success) {
                _navEvent.send(true)
            }
        }
    }
}