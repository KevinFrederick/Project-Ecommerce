package com.kevinfreyap.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.user.UserProfile
import com.kevinfreyap.core.domain.usecase.auth.AuthUseCase
import com.kevinfreyap.core.domain.usecase.voucher.VoucherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    voucherUseCase: VoucherUseCase,
    private val authUseCase: AuthUseCase
) : ViewModel() {

    val userProfile: StateFlow<Resource<UserProfile>> = authUseCase.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    private val _updateState = MutableStateFlow<Resource<Unit>?>(null)
    val updateState: StateFlow<Resource<Unit>?> = _updateState

    val newVoucherCount: StateFlow<Int> = voucherUseCase.getNewVoucherCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    init {
        refreshProfileData()
    }

    fun  refreshProfileData() {
        viewModelScope.launch {
            authUseCase.refreshUserProfile()
        }
    }

    fun updateName(currentName: String, newName: String) {
        if (currentName == newName) {
            _updateState.value = Resource.Success(Unit)
            return
        }

        viewModelScope.launch {
            authUseCase.updateUserName(newName).collect { result ->
                _updateState.value = result
            }
        }
    }

    fun updateAddress(
        currentAddress: UserAddress?,
        newStreet: String,
        newCity: String,
        newState: String,
        newZip: String,
        newCountry: String
    ) {
        val newAddress = UserAddress(
            street = newStreet,
            city = newCity,
            state = newState,
            country = newCountry,
            zipCode = newZip
        )

        val current = currentAddress ?: UserAddress()

        if (current == newAddress) {
            _updateState.value = Resource.Success(Unit)
            return
        }

        viewModelScope.launch {
            authUseCase.updateAddress(newAddress).collect { result ->
                _updateState.value = result
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = null
    }
}