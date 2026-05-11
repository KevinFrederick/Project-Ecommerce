package com.kevinfreyap.ecommerce.notification

import com.kevinfreyap.core.domain.notification.INotificationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationServiceModule {
    @Binds
    abstract fun bindNotificationService(notificationService: NotificationServiceImpl): INotificationService
}