package com.example.keepr.ui.components

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.keepr.ui.navigation.NavRoute

@Composable
fun KeeprBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    NavigationBar {
        NavRoute.bottomDestinations.forEach { dest ->
            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
            val icon = when (dest) {
                NavRoute.Collections -> Icons.Filled.Collections
                NavRoute.Add         -> Icons.Filled.FavoriteBorder
                NavRoute.Profile     -> Icons.Filled.QueryStats
                NavRoute.Items -> TODO()
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    Log.d("BottomBar", "Tapped: ${dest.route}")
                    navController.navigate(dest.route) {
                        // ensure we switch tabs instead of stacking
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false   // turn OFF for now to avoid restore loops
                        }
                        launchSingleTop = true
                        restoreState = false   // turn OFF for now
                    }
                },
                icon = { Icon(icon, contentDescription = dest.label) },
                label = { Text(dest.label) }
            )
        }
    }
}
