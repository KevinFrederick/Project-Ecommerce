package com.kevinfreyap.core.domain.services

interface INotificationService {
    fun showNotification(title: String, message: String, type: String)
}