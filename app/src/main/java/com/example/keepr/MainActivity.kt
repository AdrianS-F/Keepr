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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Vis splash før innhold
        installSplashScreen()
        super.onCreate(savedInstanceState)

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
