package com.kevinfreyap.ecommerce.di

import android.content.Context
import com.kevinfreyap.core.domain.services.INotificationService
import com.kevinfreyap.shared_ui.notification.NotificationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationService @Inject constructor(
    @param:ApplicationContext private val context: Context
): INotificationService {
    override fun showNotification(title: String, message: String, type: String) {
        NotificationHelper.showNotification(context, title, message, type)
    }
}