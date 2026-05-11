package com.kevinfreyap.core.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {



    companion object {

    }
}