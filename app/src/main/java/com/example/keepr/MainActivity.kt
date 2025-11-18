package com.example.keepr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.keepr.data.AuthRepository
import com.example.keepr.data.KeeprDatabase
import com.example.keepr.data.SessionManager
import com.example.keepr.ui.theme.KeeprTheme
import com.example.keepr.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.example.keepr.data.SettingsManager
import java.util.Locale
import android.content.res.Configuration
import android.content.Context

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val lang = com.example.keepr.data.SettingsManager(this).getLanguage()
        applyLocale(this, lang)

        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            KeeprDatabase::class.java,
            "keepr.db"
        ).fallbackToDestructiveMigration().build()

        val repo = AuthRepository(db)
        val session = SessionManager(applicationContext)
        val authVm = AuthViewModel(repo, session)

        setContent {
            KeeprTheme {
                com.example.keepr.ui.KeeprApp(
                    authVm = authVm,
                    session = session
                )
            }
        }
    }
}

fun applyLocale(context: Context, lang: String) {
    val locale = Locale(lang)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
