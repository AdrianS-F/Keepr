package com.example.keepr.ui.components

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
import androidx.navigation.NavHostController
import com.example.keepr.ui.navigation.NavRoute
import androidx.compose.ui.res.stringResource
import com.example.keepr.R

@Composable
fun KeeprBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    // Hardkodet, ikke-null liste: kun hovedruter
    val tabs: List<NavRoute> = listOf(
        NavRoute.Collections,
        NavRoute.Add,
        NavRoute.Profile
    )

    NavigationBar {
        tabs.forEach { dest ->
            // ekstra belt-and-suspenders guard (skulle aldri skje nå):
            if (dest == null) return@forEach

            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true

            val icon = when (dest) {
                NavRoute.Collections -> Icons.Filled.Collections
                NavRoute.Add         -> Icons.Filled.FavoriteBorder
                NavRoute.Profile     -> Icons.Filled.QueryStats
                else                 -> Icons.Filled.Collections
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(dest.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        icon,
                        contentDescription = when (dest) {
                            NavRoute.Collections -> stringResource(R.string.nav_collections)
                            NavRoute.Add         -> stringResource(R.string.nav_add)
                            NavRoute.Profile     -> stringResource(R.string.nav_profile)
                            else                 -> dest.label
                        }
                    )
                },
                label = {
                    Text(
                        text = when (dest) {
                            NavRoute.Collections -> stringResource(R.string.nav_collections)
                            NavRoute.Add         -> stringResource(R.string.nav_add)
                            NavRoute.Profile     -> stringResource(R.string.nav_profile)
                            else                 -> dest.label
                        }
                    )
                }
            )
        }
    }
}
