package com.example.keepr.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import com.example.keepr.R
import com.example.keepr.ui.navigation.NavRoute

@Composable
fun KeeprBottomBar(
    navController: NavHostController
) {
    val tabs = listOf(
        NavRoute.Collections,
        NavRoute.Add,
        NavRoute.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        tabs.forEach { dest ->
            val selected = when (dest) {
                NavRoute.Collections ->
                    currentRoute?.startsWith(NavRoute.Collections.route) == true
                NavRoute.Add ->
                    currentRoute?.startsWith(NavRoute.Add.route) == true
                NavRoute.Profile ->
                    currentRoute?.startsWith(NavRoute.Profile.route) == true
                else -> false
            }

            val icon = when (dest) {
                NavRoute.Collections -> Icons.Filled.Collections
                NavRoute.Add         -> Icons.Filled.AddCircleOutline
                NavRoute.Profile     -> Icons.Filled.Person
                else                 -> Icons.Filled.Collections
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(dest.route) {
                            popUpTo(NavRoute.Collections.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(icon, contentDescription = dest.label) },
                label = {
                    val labelText = when (dest) {
                        NavRoute.Collections -> stringResource(R.string.nav_collections)
                        NavRoute.Add         -> stringResource(R.string.nav_add)
                        NavRoute.Profile     -> stringResource(R.string.nav_profile)
                        else                 -> dest.label
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

