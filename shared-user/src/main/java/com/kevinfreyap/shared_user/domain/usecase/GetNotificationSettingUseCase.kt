package com.kevinfreyap.shared_user.domain.usecase

import com.kevinfreyap.core.domain.notification.NotificationPreferences
import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationSettingUseCase @Inject constructor(
    private val userRepository: IUserRepository
){
    operator fun invoke(): Flow<NotificationPreferences> = userRepository.getNotificationSettings()
}