package com.ultratechies.ghala.data.models

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.ultratechies.ghala.domain.models.UserModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ghala_data")

class AppDatasource @Inject constructor(@ApplicationContext context: Context, val gson: Gson) {
    private val applicationContext = context.applicationContext

    companion object {
        val USER_KEY = stringPreferencesKey("user")
        val ACCESS_TOKEN = stringPreferencesKey("access")
        val REFRESH_TOKEN = stringPreferencesKey("refresh")
    }

    suspend fun saveUserToPreferencesStore(userModel: UserModel) {
        val userModelString = gson.toJson(userModel)
        applicationContext.dataStore.edit { preferences ->
            preferences[USER_KEY] = userModelString
        }
    }

    fun getUserFromPreferencesStore(): Flow<UserModel> = applicationContext.dataStore.data
        .map { preferences ->
            val userModelString = preferences[USER_KEY]
            gson.fromJson(userModelString, UserModel::class.java)
        }

    suspend fun saveAccessToken(accessToken: String) {
        applicationContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    fun getAccessToken(): Flow<String?> = applicationContext.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }

    fun getPrefs() = applicationContext.dataStore.data

    suspend fun saveRefreshToken(refreshToken: String) {
        applicationContext.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    fun getRefreshToken(): Flow<String?> = applicationContext.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN]
    }

    fun clear() = runBlocking {
        applicationContext.dataStore.edit {
            it.clear()
        }
    }

}