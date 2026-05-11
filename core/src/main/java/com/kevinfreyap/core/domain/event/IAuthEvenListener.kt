package com.kevinfreyap.core.domain.event

interface IAuthEvenListener {
    suspend fun onUserLoggedIn()
    suspend fun onUserLoggedOut()
}