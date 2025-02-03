package com.example.composetutorial


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MainViewModel(private val context: Context) : ViewModel() {


    private val dataStore: DataStore<Preferences> = context.dataStore

    constructor() : this(MyApplicationClass.getAppContext()) {
    }

    companion object {
        private lateinit var appContext: Context

        private fun getAppContext(): Context {
            if (!::appContext.isInitialized) {
                appContext = MyApplicationClass.instance.applicationContext
            }
            return appContext
        }

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userPreferences")
        // define the username & profile pics
        private val userNameKey = stringPreferencesKey("name")
        private val userProfilePicUrlKey = stringPreferencesKey("profilePicUrl")
    }


    val userName: Flow<String> = dataStore.data.map { preferences ->
        preferences[userNameKey] ?: "DefaultName"
    }
    val userProfilePicture: Flow<String> = dataStore.data.map { preferences ->
        preferences[userProfilePicUrlKey] ?: "DefaultProfilePicUrl"
    }



    private suspend fun editPreferences(transform: suspend (MutablePreferences) -> Unit) {
        dataStore.edit { preferences ->
            transform(preferences)
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            editPreferences { preferences ->
                preferences[userNameKey] = newName
            }
        }
    }

    fun updateUserProfilePicture(newUrl: String) {
        viewModelScope.launch {
            editPreferences { preferences ->
                preferences[userProfilePicUrlKey] = newUrl
            }
        }
    }

}


