package com.example.keepr.data


import android.content.Context
import android.content.Context.MODE_PRIVATE

class SettingsManager (context: Context) {
    private val prefs = context.getSharedPreferences("keepr_settings", MODE_PRIVATE)

    fun setLanguage(lang: String){
        prefs.edit().putString("app_lang", lang).apply()
    }

    fun getLanguage(): String = prefs.getString("app_lang", "en") ?: "en"
}