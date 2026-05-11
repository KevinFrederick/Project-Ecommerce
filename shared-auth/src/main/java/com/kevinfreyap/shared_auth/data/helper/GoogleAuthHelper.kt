package com.kevinfreyap.shared_auth.data.helper

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthHelper(
    private val context: Context,
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(serverClientId: String): String? {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                return googleIdTokenCredential.idToken
            }
        } catch (e: GetCredentialException) {
            Log.e("GoogleAuth", "Sign in failed: ${e.message}")
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Unknown Error", e)
        }
        return null
    }

    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Sign out failed", e)
        }
    }
}