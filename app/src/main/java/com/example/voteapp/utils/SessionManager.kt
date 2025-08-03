package com.example.voteapp.utils

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
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
}