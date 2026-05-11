package com.kevinfreyap.shared_user.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(newName: String): Flow<Resource<Unit>> = flow {
        if (newName.isBlank()) {
            emit(Resource.Error("ERROR_NO_NAME"))
            return@flow
        }

        if (newName.length > 30) {
            emit(Resource.Error("ERROR_NAME_TOO_LONG"))
            return@flow
        }

        emitAll(userRepository.updateUserName(newName))
    }
}