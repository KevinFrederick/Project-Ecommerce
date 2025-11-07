package com.kevinfreyap.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.user.UserProfile
import com.kevinfreyap.core.domain.usecase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _userProfile = MutableStateFlow<Resource<UserProfile?>>(Resource.Loading())
    val userProfile: StateFlow<Resource<UserProfile?>> = _userProfile

    init {
        loadUserProfile()
    }

    private fun  loadUserProfile() {
        viewModelScope.launch {
            authUseCase.getUserProfile().collect { resource ->
                _userProfile.value = resource
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authUseCase.logout()
            _userProfile.value = Resource.Success(null)
        }
    }
}