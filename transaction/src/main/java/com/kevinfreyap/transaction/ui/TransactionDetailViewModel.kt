package com.kevinfreyap.transaction.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import com.kevinfreyap.shared_transaction.domain.usecase.GetTransactionByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    getTransactionById: GetTransactionByIdUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val transactionId = savedStateHandle.get<String>("transactionId")

    val transaction: StateFlow<Resource<TransactionReceipt>> =
        if (transactionId.isNullOrEmpty()) {
            MutableStateFlow(
                Resource.Error("ERROR_TRANSACTION_ID_NOT_FOUND")
            )
        } else {
            getTransactionById(transactionId).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading()
            )
        }
}