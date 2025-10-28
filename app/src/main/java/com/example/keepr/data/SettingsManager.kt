package com.example.keepr.data // Tells us what package it is from

// Gets us needed android classes
import android.content.Context // gives us access to saving, files
import android.content.Context.MODE_PRIVATE // Saving is only for this app and this app only

class SettingsManager (context: Context) { // making a class named SettingsManager. Also allows saving
    private val prefs = context.getSharedPreferences("keepr_settings", MODE_PRIVATE) // making a sharedprefrance file called keepr_settings

    fun setLanguage(lang: String){ // our save function when a user presses save
        prefs.edit().putString("app_lang", lang).apply()
    }

    fun getLanguage(): String = prefs.getString("app_lang", "en") ?: "en" // making english the standard
}