package com.example.mahirexpress.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MahirExpressPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_THEME_DARK = "theme_dark"
    }

    fun saveData(userId: String, name: String, email: String, role: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_FULL_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_ROLE, role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUserData(): Map<String, String?> {
        return mapOf(
            "userId" to sharedPreferences.getString(KEY_USER_ID, ""),
            "name" to sharedPreferences.getString(KEY_FULL_NAME, ""),
            "email" to sharedPreferences.getString(KEY_EMAIL, ""),
            "role" to sharedPreferences.getString(KEY_ROLE, "Customer")
        )
    }

    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_THEME_DARK, enabled).apply()
    }

    fun isDarkMode(): Boolean = sharedPreferences.getBoolean(KEY_THEME_DARK, false)

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
