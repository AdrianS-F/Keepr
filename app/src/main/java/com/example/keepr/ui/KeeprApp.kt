package com.example.keepr.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.keepr.ui.components.KeeprBottomBar
import com.example.keepr.ui.navigation.NavRoute
import com.example.keepr.ui.screens.collections.CollectionsScreen
import com.example.keepr.ui.screens.settings.SettingsScreen
import com.example.keepr.ui.screens.stats.StatsScreen
import com.example.keepr.ui.screens.wishlist.WishlistScreen

@Composable
fun KeeprApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = { KeeprBottomBar(navController, currentDestination) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Collections.route
        ) {
            composable(NavRoute.Collections.route) { CollectionsScreen(padding) }
            composable(NavRoute.Wishlist.route)   { WishlistScreen(padding) }
            composable(NavRoute.Stats.route)      { StatsScreen(padding) }
            composable(NavRoute.Settings.route)   { SettingsScreen(padding) }
        }
    }
}
