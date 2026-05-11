package com.kevinfreyap.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.account.model.UserAddressUi
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_user.domain.model.UserAddress
import com.kevinfreyap.shared_user.domain.model.UserProfile
import com.kevinfreyap.shared_user.domain.usecase.GetUserProfileUseCase
import com.kevinfreyap.shared_user.domain.usecase.RefreshUserProfileUseCase
import com.kevinfreyap.shared_user.domain.usecase.UpdateAddressUseCase
import com.kevinfreyap.shared_user.domain.usecase.UpdateUserNameUseCase
import com.kevinfreyap.shared_voucher.domain.usecase.GetNewVoucherCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    getNewVoucherCount: GetNewVoucherCountUseCase,
    getUserProfile: GetUserProfileUseCase,
    private val updateUserName: UpdateUserNameUseCase,
    private val updateUserAddress: UpdateAddressUseCase,
    private val refreshUserProfile: RefreshUserProfileUseCase,
) : ViewModel() {

    val userProfile: StateFlow<Resource<UserProfile>> = getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    private val _updateState = MutableStateFlow<Resource<Unit>?>(null)
    val updateState: StateFlow<Resource<Unit>?> = _updateState

    val newVoucherCount: StateFlow<Int> = getNewVoucherCount()
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
            refreshUserProfile()
        }
    }

    fun updateName(currentName: String, newName: String) {
        if (currentName == newName) {
            _updateState.value = Resource.Success(Unit)
            return
        }

        viewModelScope.launch {
            updateUserName(newName).collect { result ->
                _updateState.value = result
            }
        }
    }

    fun updateAddress(
        currentAddress: UserAddressUi?,
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
            updateUserAddress(
                newStreet = newStreet,
                newCity = newCity,
                newState = newState,
                newZip = newZip,
                newCountry = newCountry
            ).collect { result ->
                _updateState.value = result
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = null
    }
}