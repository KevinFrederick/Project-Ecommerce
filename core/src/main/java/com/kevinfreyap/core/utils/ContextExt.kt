package com.kevinfreyap.core.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun Context.isOnline(): Boolean {
    val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun Context.isDarkThemeOn(): Boolean {
    val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
}