package com.kevinfreyap.core.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun FirebaseAuth.getAuthUidFlow(): Flow<String?> {
    return callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }

        addAuthStateListener(listener)
        awaitClose { removeAuthStateListener(listener) }
    }
}

fun FirebaseUser.isGoogleAccount(): Boolean {
    return this.providerData.any { userInfo ->
        userInfo.providerId == GoogleAuthProvider.PROVIDER_ID
    }
}