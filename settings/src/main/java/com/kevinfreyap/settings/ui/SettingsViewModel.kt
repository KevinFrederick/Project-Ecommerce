package com.kevinfreyap.settings.ui

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.notification.NotificationPreferences
import com.kevinfreyap.shared_auth.domain.usecase.DeleteWithGoogleUseCase
import com.kevinfreyap.shared_auth.domain.usecase.DeleteWithPassUseCase
import com.kevinfreyap.shared_auth.domain.usecase.LogoutUseCase
import com.kevinfreyap.shared_auth.domain.usecase.UpdatePasswordUseCase
import com.kevinfreyap.shared_user.domain.model.UserProfile
import com.kevinfreyap.shared_user.domain.usecase.GetNotificationSettingUseCase
import com.kevinfreyap.shared_user.domain.usecase.GetThemeUseCase
import com.kevinfreyap.shared_user.domain.usecase.GetUserProfileUseCase
import com.kevinfreyap.shared_user.domain.usecase.SaveThemeUseCase
import com.kevinfreyap.shared_user.domain.usecase.UpdateNotificationSettingUseCase
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
    getUserProfile: GetUserProfileUseCase,
    getTheme: GetThemeUseCase,
    getNotificationSetting: GetNotificationSettingUseCase,
    private val saveTheme: SaveThemeUseCase,
    private val updateNotificationSetting: UpdateNotificationSettingUseCase,
    private val deleteWithPassUseCase: DeleteWithPassUseCase,
    private val deleteWithGoogleUseCase: DeleteWithGoogleUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val logoutUseCase: LogoutUseCase,
): ViewModel(){

    val currentUser: StateFlow<Resource<UserProfile>> = getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    val currentTheme: StateFlow<Int> = getTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

    val notificationSettings = getNotificationSetting()
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
            saveTheme(mode)
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    fun updateNotificationPreferences(isSystem: Boolean, isEnabled: Boolean) {
        viewModelScope.launch {
            updateNotificationSetting(isSystem, isEnabled)
        }
    }

    fun onChangePassword(currentPass: String, newPass: String, confirmPass: String) {
        viewModelScope.launch {
            _updateState.value = Resource.Loading()

            val result = updatePasswordUseCase(
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
                logoutUseCase()

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
            val result = deleteWithPassUseCase(password)
            _deleteState.value = result
            if (result is Resource.Success) {
                _navEvent.send(true)
            }
        }
    }

    fun onDeleteAccountWithGoogle(idToken: String) {
        viewModelScope.launch {
            _deleteState.value = Resource.Loading()
            val result = deleteWithGoogleUseCase(idToken)
            _deleteState.value = result
            if (result is Resource.Success) {
                _navEvent.send(true)
            }
        }
    }
}