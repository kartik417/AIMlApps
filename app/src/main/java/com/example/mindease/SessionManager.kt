package com.example.mindease


import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveLogin(email: String) {
        prefs.edit().putString("email", email).putBoolean("is_logged_in", true).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString("email", null)
    }
}
