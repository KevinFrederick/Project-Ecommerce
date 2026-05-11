package com.kevinfreyap.core.domain.notification

data class NotificationPreferences(
    val system: Boolean = true,
    val promotions: Boolean = true
)
