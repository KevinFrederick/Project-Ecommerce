package com.kevinfreyap.transaction.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_cart.domain.usecase.GetCartItemCountUseCase
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import com.kevinfreyap.shared_transaction.domain.usecase.GetTransactionHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    getTransactionHistory: GetTransactionHistoryUseCase,
    getCartItemCount: GetCartItemCountUseCase,
): ViewModel() {
    val transactionList: StateFlow<Resource<List<TransactionReceipt>>> = getTransactionHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    val cartItemCount: StateFlow<Int> = getCartItemCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )
}