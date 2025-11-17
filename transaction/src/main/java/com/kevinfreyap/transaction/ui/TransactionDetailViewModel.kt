package com.kevinfreyap.transaction.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.order.OrderReceipt
import com.kevinfreyap.core.domain.usecase.transaction.TransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    transactionUseCase: TransactionUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val transactionId = savedStateHandle.get<String>("transactionId")

    val transaction: StateFlow<Resource<OrderReceipt>> =
        if (transactionId.isNullOrEmpty()) {
            MutableStateFlow(
                Resource.Error("ERROR_TRANSACTION_ID_NOT_FOUND")
            )
        } else {
            transactionUseCase.getTransactionById(transactionId).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading()
            )
        }
}