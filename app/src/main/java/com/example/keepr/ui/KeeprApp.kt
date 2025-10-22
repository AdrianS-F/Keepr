package com.example.keepr.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.keepr.ui.components.KeeprBottomBar
import com.example.keepr.ui.navigation.NavRoute
import com.example.keepr.ui.screens.collections.CollectionsScreen
import com.example.keepr.ui.screens.items.ItemsScreen
import com.example.keepr.ui.screens.add.AddScreen
import com.example.keepr.ui.screens.profile.ProfileScreen

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
            composable(NavRoute.Collections.route) {
                CollectionsScreen(
                    padding = padding,
                    onOpen = { collectionId ->
                        navController.navigate(NavRoute.Items.makeRoute(collectionId))

                    }
                )
            }



            composable(NavRoute.Add.route)   { AddScreen(padding) }
            composable(NavRoute.Profile.route)      { ProfileScreen(padding) }

            composable(
                route = NavRoute.Items.route,
                arguments = listOf(navArgument("collectionId") { type = NavType.LongType })
            ) { entry ->
                val cid = entry.arguments!!.getLong("collectionId")
                ItemsScreen(
                    padding = padding,
                    collectionId = cid,
                    onBack = { navController.popBackStack()},
                    navController = navController
                )
            }

        }
    }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, dest, _ ->
            android.util.Log.d("Nav", "Now at route: ${dest.route}")
        }
    }

}
