package com.kevinfreyap.auth.ui.nav

sealed interface AuthNav {
    object ToAccount: AuthNav
    object ToLogin: AuthNav
}