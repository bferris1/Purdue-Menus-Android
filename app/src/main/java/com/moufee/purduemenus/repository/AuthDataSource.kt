package com.moufee.purduemenus.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

private const val KEY_LOGGED_IN = "logged_in"
private const val KEY_USERNAME = "username"

class AuthDataSource @Inject constructor(private val sharedPreferences: SharedPreferences) {
    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_LOGGED_IN, false)

    fun setLoggedIn(username: String) {
        sharedPreferences.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_USERNAME, username)
                .apply()
    }

    fun setLoggedOut() {
        sharedPreferences.edit {
            putBoolean(KEY_LOGGED_IN, false)
        }
    }

    fun getUserName(): String? = sharedPreferences.getString(KEY_USERNAME, null)
}