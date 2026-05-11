package com.kevinfreyap.core.domain.notification

interface INotificationService {
    fun showNotification(title: String, message: String, type: String)
}