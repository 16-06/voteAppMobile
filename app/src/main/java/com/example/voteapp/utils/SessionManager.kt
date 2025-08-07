package com.example.voteapp.utils

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val PREF_NAME = "user_prefs"
    private val KEY_ID = "userId"
    private val KEY_USERNAME = "username"

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String){
        prefs.edit { putString("jwt_token", token) }
    }

    fun getToken(): String?{
        return prefs.getString("jwt_token", null)
    }

    fun clearToken(){
        prefs.edit { remove("jwt_token") }
    }

    fun saveUser(context: Context, id: Long, username: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putLong(KEY_ID, id)
                .putString(KEY_USERNAME, username)
        }
    }

    fun getUserId(context: Context): Long {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_ID, -1)
    }

    fun getUsername(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USERNAME, null)
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit { clear() }
    }
}