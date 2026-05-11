package com.kevinfreyap.shared_user.data.source.local

import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.kevinfreyap.core.domain.notification.NotificationPreferences
import com.kevinfreyap.shared_user.domain.model.UserAddress
import com.kevinfreyap.shared_user.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
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
                address = addressObj,
                isGoogleAccount = preferences[IS_GOOGLE_KEY] ?: false
            )
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        dataStore.edit { preferences ->
            preferences[USER_UID] = profile.uid
            preferences[USERNAME_KEY] = profile.displayName ?: ""
            preferences[EMAIL_KEY] = profile.email ?: ""
            preferences[PHOTO_URL_KEY] = profile.photoUrl ?: ""
            preferences[IS_GOOGLE_KEY] = profile.isGoogleAccount

            if (profile.address != null){
                preferences[ADDRESS_KEY] = Gson().toJson(profile.address)
            }
        }
    }

    suspend fun clearLocalUserProfile() {
        dataStore.edit { preferences ->
            val currentTheme = preferences[THEME_KEY]

            preferences.clear()

            if (currentTheme != null) {
                preferences[THEME_KEY] = currentTheme
            }
        }
    }

    fun getNotificationSettings(): Flow<NotificationPreferences> {
        return dataStore.data.map { preferences ->
            NotificationPreferences(
                system = preferences[NOTIF_SYSTEM_KEY] ?: true,
                promotions = preferences[NOTIF_PROMOTION_KEY] ?: true
            )
        }
    }

    suspend fun saveNotificationSettings(isSystem: Boolean, isEnabled: Boolean) {
        val key = if (isSystem) NOTIF_SYSTEM_KEY else NOTIF_PROMOTION_KEY
        dataStore.edit { preferences ->
            preferences[key] = isEnabled
        }
    }

    val themeMode: Flow<Int> = dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    suspend fun saveTheme(mode: Int) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode
        }
    }

    companion object {
        private val THEME_KEY = intPreferencesKey("theme_mode")
        private val USER_UID = stringPreferencesKey("uid")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PHOTO_URL_KEY = stringPreferencesKey("photo_url")
        private val ADDRESS_KEY = stringPreferencesKey("address")
        private val IS_GOOGLE_KEY = booleanPreferencesKey("is_google")
        private val NOTIF_SYSTEM_KEY = booleanPreferencesKey("notif_system")
        private val NOTIF_PROMOTION_KEY = booleanPreferencesKey("notif_promo")
    }
}