package com.example.mahirexpress.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PrefManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MahirExpressPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_ROLE = "role"
        private const val KEY_IS_DARK_MODE = "dark_mode"
    }

    fun saveUser(userId: String, name: String, email: String, phone: String, role: String) {
        prefs.edit {
            putString(KEY_USER_ID, userId)
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
            putString(KEY_ROLE, role)
        }
    }

    fun getUserRole(): String = prefs.getString(KEY_ROLE, "customer") ?: "customer"
    fun getUserName(): String = prefs.getString(KEY_NAME, "") ?: ""
    fun getUserEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""
    fun getUserPhone(): String = prefs.getString(KEY_PHONE, "") ?: ""

    fun setDarkMode(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_IS_DARK_MODE, enabled) }
    }

    fun isDarkMode(): Boolean = prefs.getBoolean(KEY_IS_DARK_MODE, false)

    fun clear() {
        prefs.edit { clear() }
    }
}
