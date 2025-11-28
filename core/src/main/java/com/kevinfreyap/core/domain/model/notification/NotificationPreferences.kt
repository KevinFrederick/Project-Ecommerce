package com.kevinfreyap.core.domain.model.notification

data class NotificationPreferences(
    val system: Boolean = true,
    val promotions: Boolean = true
)
