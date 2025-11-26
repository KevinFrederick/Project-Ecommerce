package com.kevinfreyap.settings.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.domain.usecase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
): ViewModel(){

    private val _navEvent = Channel<Boolean>()
    val navEvent = _navEvent.receiveAsFlow()

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