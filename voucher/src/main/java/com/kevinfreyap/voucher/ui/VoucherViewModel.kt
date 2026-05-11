package com.kevinfreyap.voucher.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_voucher.domain.usecase.GetUserVoucherUseCase
import com.kevinfreyap.shared_voucher.domain.usecase.MarkAllVoucherSeenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoucherViewModel @Inject constructor(
    getUserVoucher: GetUserVoucherUseCase,
    private val markAllVoucherSeen: MarkAllVoucherSeenUseCase
): ViewModel() {

    val voucherList = getUserVoucher()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    fun markAllAsSeen() {
        viewModelScope.launch {
            markAllVoucherSeen()
        }
    }
}