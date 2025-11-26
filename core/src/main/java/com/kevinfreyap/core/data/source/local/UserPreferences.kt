package com.kevinfreyap.core.data.source.local

import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.google.gson.Gson
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.user.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val themeMode: Flow<Int> = dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    suspend fun saveTheme(mode: Int) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode
        }
    }

    fun getAuthToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }
    }

    fun getUserProfile(): Flow<UserProfile> {
        return dataStore.data.map { preferences ->
            val addressJson = preferences[ADDRESS_KEY]

            val addressObj = if (addressJson != null) {
                Gson().fromJson(addressJson, UserAddress::class.java)
            } else {
                null
            }
            UserProfile(
                uid = preferences[USER_UID] ?: "",
                email = preferences[EMAIL_KEY],
                displayName = preferences[USERNAME_KEY],
                photoUrl = preferences[PHOTO_URL_KEY],
                address = addressObj
            )
        }
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        dataStore.edit { preferences ->
            preferences[USER_UID] = profile.uid
            preferences[USERNAME_KEY] = profile.displayName ?: ""
            preferences[EMAIL_KEY] = profile.email ?: ""
            preferences[PHOTO_URL_KEY] = profile.photoUrl ?: ""

            if (profile.address != null){
                preferences[ADDRESS_KEY] = Gson().toJson(profile.address)
            }
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            val currentTheme = preferences[THEME_KEY]

            preferences.clear()

            if (currentTheme != null) {
                preferences[THEME_KEY] = currentTheme
            }
        }
    }

    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_UID = stringPreferencesKey("uid")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PHOTO_URL_KEY = stringPreferencesKey("photo_url")
        private val ADDRESS_KEY = stringPreferencesKey("address")
        private val THEME_KEY = intPreferencesKey("theme_mode")
    }
}