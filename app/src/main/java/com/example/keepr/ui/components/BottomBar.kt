package com.example.keepr.ui.components

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.keepr.R
import com.example.keepr.ui.navigation.NavRoute

@Composable
fun KeeprBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    val tabs = listOf(
        NavRoute.Collections,
        NavRoute.Add,
        NavRoute.Profile
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        tabs.forEach { dest ->
            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true

            val icon = when (dest) {
                NavRoute.Collections -> Icons.Filled.Collections
                NavRoute.Add         -> Icons.Filled.AddCircleOutline
                NavRoute.Profile     -> Icons.Filled.Person
                // 'else' for å tilfredsstille en forvirret kompilator.
                else -> Icons.Filled.Collections
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        Log.d("BottomBar", "Tapped: ${dest.route}")
                        navController.navigate(dest.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        icon,
                        contentDescription = dest.label
                    )
                },
                label = {
                    val labelText = when (dest) {
                        NavRoute.Collections -> stringResource(R.string.nav_collections)
                        NavRoute.Add         -> stringResource(R.string.nav_add)
                        NavRoute.Profile     -> stringResource(R.string.nav_profile)
                        // 'else' for å tilfredsstille en forvirret kompilator.
                        else -> dest.label
                    }
                    Text(text = labelText)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
