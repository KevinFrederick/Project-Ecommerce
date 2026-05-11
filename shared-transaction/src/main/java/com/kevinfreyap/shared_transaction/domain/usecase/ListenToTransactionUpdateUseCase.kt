package com.kevinfreyap.shared_transaction.domain.usecase

import com.kevinfreyap.shared_transaction.domain.repository.ITransactionRepository
import javax.inject.Inject

class ListenToTransactionUpdateUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository
) {
    operator fun invoke() = transactionRepository.listenToTransactionUpdates()
}