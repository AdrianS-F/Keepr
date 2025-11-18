package com.example.keepr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.room.Room
import com.example.keepr.data.AuthRepository
import com.example.keepr.data.KeeprDatabase
import com.example.keepr.data.SessionManager
import com.example.keepr.ui.theme.KeeprTheme
import com.example.keepr.ui.viewmodel.AuthViewModel
import java.util.Locale
import android.content.res.Configuration
import android.content.Context
import com.example.keepr.notifications.NotificationHelper
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.keepr.notifications.InactivityWorker
import java.util.concurrent.TimeUnit
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val lang = com.example.keepr.data.SettingsManager(this).getLanguage()
        applyLocale(this, lang)

        super.onCreate(savedInstanceState)

        // Lagre når appen sist ble åpnet
        val prefs = getSharedPreferences("keepr_prefs", Context.MODE_PRIVATE)
        prefs.edit().putLong("last_opened", System.currentTimeMillis()).apply()

        // Vis splash før innhold
        installSplashScreen()

        // Opprett notification-kanal
        NotificationHelper.createChannel(this)
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
        // Start bakgrunnsjobb som sjekker inaktivitet
        val workRequest = PeriodicWorkRequestBuilder<InactivityWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "inactivity_check",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )

        // Edge-to-edge
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
