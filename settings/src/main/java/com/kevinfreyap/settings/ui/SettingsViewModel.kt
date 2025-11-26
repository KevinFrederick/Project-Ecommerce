package com.kevinfreyap.settings.ui

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.domain.usecase.auth.AuthUseCase
import com.kevinfreyap.core.domain.usecase.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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

    val currentTheme: StateFlow<Int> = userUseCase.getTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

    private val _navEvent = Channel<Boolean>()
    val navEvent = _navEvent.receiveAsFlow()

    fun setTheme(mode: Int) {
        viewModelScope.launch {
            userUseCase.saveTheme(mode)
            AppCompatDelegate.setDefaultNightMode(mode)
        }
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
}