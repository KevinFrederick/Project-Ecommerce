package com.kevinfreyap.account.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.user.UserProfile
import com.kevinfreyap.core.domain.usecase.auth.AuthUseCase
import com.kevinfreyap.core.domain.usecase.cart.CartUseCase
import com.kevinfreyap.core.domain.usecase.transaction.TransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val cartUseCase: CartUseCase,
    private val transactionUseCase: TransactionUseCase
) : ViewModel() {

    val userProfile: StateFlow<Resource<UserProfile>> = authUseCase.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    private val _updateNameState = MutableStateFlow<Resource<Unit>?>(null)
    val updateNameState: StateFlow<Resource<Unit>?> = _updateNameState

    init {
        refreshProfileData()
    }

    fun  refreshProfileData() {
        viewModelScope.launch {
            authUseCase.refreshUserProfile()
        }
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            authUseCase.updateUserName(name).collect { result ->
                _updateNameState.value = result
            }
        }
    }

    fun resetUpdateState() {
        _updateNameState.value = null
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authUseCase.logout()
                cartUseCase.clearCart()
                transactionUseCase.clearOrderHistory()
            } catch (e: IOException) {
                Log.e("AccountViewModel", "Failed to clear auth token", e)
            } catch (e: Exception) {
                Log.e("AccountViewModel", "Logout Failed", e)
            }
        }
    }
}