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

        // Vis splash før innhold
        installSplashScreen()
        // Kjør evt. seeding før UI (ikke blokkér main-tråden)
        lifecycleScope.launch {
            com.example.keepr.seed.seedDemoIfNeeded(this@MainActivity)
        }

        // Edge-to-edge
        enableEdgeToEdge()

        // ---- Init av avhengigheter (enkelt, uten DI-rammeverk) ----
        val db = Room.databaseBuilder(
            applicationContext,
            KeeprDatabase::class.java,
            "keepr.db"
        ).fallbackToDestructiveMigration().build()

        val repo = AuthRepository(db)
        val session = SessionManager(applicationContext)
        val authVm = AuthViewModel(repo, session)

        // ---- UI ----
        setContent {
            KeeprTheme {
                // Pass videre til appen din (oppdater signaturen til KeeprApp under)
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
