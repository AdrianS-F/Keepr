package com.example.keepr.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.keepr.data.SessionManager
import com.example.keepr.ui.components.KeeprBottomBar
import com.example.keepr.ui.navigation.NavRoute
import com.example.keepr.ui.screens.add.AddScreen
import com.example.keepr.ui.screens.collections.CollectionsScreen
import com.example.keepr.ui.screens.items.ItemsScreen
import com.example.keepr.ui.screens.profile.ProfileScreen
import com.example.keepr.ui.viewmodel.AuthViewModel

// Auth-ruter
private const val ROUTE_SIGNUP = "signup"
private const val ROUTE_SIGNIN = "signin"

@Composable
fun KeeprApp(
    authVm: AuthViewModel,
    session: SessionManager
) {
    val navController = rememberNavController()

    // Observér om en bruker er innlogget
    val userId by session.loggedInUserId.collectAsState(initial = null)
    val isLoggedIn = userId != null

    // Rebuild nav-graf når login-status endrer seg, så startDest fungerer riktig
    key(userId) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination

        // Vis bottom bar kun på hovedrutene (ikke på auth)
        val showBottomBar = when (currentDestination?.route) {
            NavRoute.Collections.route,
            NavRoute.Add.route,
            NavRoute.Profile.route -> true
            else -> false
        }

        Scaffold(
            bottomBar = { if (showBottomBar) KeeprBottomBar(navController, currentDestination) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn) NavRoute.Collections.route else ROUTE_SIGNUP
            ) {
                // ---------- AUTH GRAF ----------
                composable(ROUTE_SIGNUP) {
                    com.example.keepr.ui.screens.auth.SignUpScreen(
                        vm = authVm,
                        onGoToSignIn = { navController.navigate(ROUTE_SIGNIN) { launchSingleTop = true } },
                        onSignedIn = {
                            navController.navigate(NavRoute.Collections.route) {
                                popUpTo(ROUTE_SIGNUP) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(ROUTE_SIGNIN) {
                    com.example.keepr.ui.screens.auth.SignInScreen(
                        vm = authVm,
                        onSignedIn = {
                            navController.navigate(NavRoute.Collections.route) {
                                popUpTo(ROUTE_SIGNIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                // ---------- MAIN GRAF ----------
                composable(NavRoute.Collections.route) {
                    CollectionsScreen(
                        padding = paddingValues,
                        onOpen = { collectionId ->
                            navController.navigate(NavRoute.Items.makeRoute(collectionId))
                        }
                    )
                }

                composable(NavRoute.Add.route) {
                    AddScreen(paddingValues)
                }

                composable(NavRoute.Profile.route) {
                    // Legg inn onLogout her når dere ønsker (kall session.clear() og nav til ROUTE_SIGNIN)
                    ProfileScreen(paddingValues)
                }

                composable(
                    route = NavRoute.Items.route,
                    arguments = listOf(navArgument("collectionId") { type = NavType.LongType })
                ) { entry ->
                    val cid = entry.arguments!!.getLong("collectionId")
                    ItemsScreen(
                        padding = paddingValues,
                        collectionId = cid,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
