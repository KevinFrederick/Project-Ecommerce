package com.kevinfreyap.account.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.user.UserProfile
import com.kevinfreyap.core.domain.usecase.auth.AuthUseCase
import com.kevinfreyap.core.domain.usecase.cart.CartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val cartUseCase: CartUseCase
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
            try {
                authUseCase.logout()
                cartUseCase.clearCartOnLogout()
                _userProfile.value = Resource.Success(null)
            } catch (e: IOException) {
                Log.e("AccountViewModel", "Failed to clear auth token", e)
                _userProfile.value = Resource.Success(null)
            } catch (e: Exception) {
                Log.e("AccountViewModel", "Logout Failed", e)
                _userProfile.value = Resource.Success(null)
            }
        }
    }
}