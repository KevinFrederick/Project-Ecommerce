package com.kevinfreyap.shared_events

sealed class AppEvent {
    object UserLoggedIn: AppEvent()
    object UserLoggedOut: AppEvent()
}