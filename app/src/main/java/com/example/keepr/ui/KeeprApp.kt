package com.example.keepr.ui

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.keepr.ui.components.KeeprBottomBar
import com.example.keepr.ui.navigation.NavRoute
import com.example.keepr.ui.screens.add.AddScreen
import com.example.keepr.ui.screens.collections.CollectionsScreen
import com.example.keepr.ui.screens.profile.ProfileScreen

@Composable
fun KeeprApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = { KeeprBottomBar(navController, currentDestination) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Collections.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoute.Collections.route) { CollectionsScreen() }
            composable(NavRoute.add.route)        { AddScreen() }
            composable(NavRoute.profile.route)    { ProfileScreen() }  // <- kun KALL her
        }
    }
}
