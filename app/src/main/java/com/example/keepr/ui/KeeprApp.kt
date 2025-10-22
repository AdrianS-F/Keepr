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
import kotlinx.coroutines.launch

// Auth routes
private const val ROUTE_SIGNUP = "signup"
private const val ROUTE_SIGNIN = "signin"

@Composable
fun KeeprApp(
    authVm: AuthViewModel,
    session: SessionManager
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // Observe logged-in user (from DataStore in SessionManager)
    val userId by session.loggedInUserId.collectAsState(initial = null)
    val isLoggedIn = userId != null

    // current route (for bottom-bar visibility)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar =
        currentRoute == NavRoute.Collections.route ||
        currentRoute == NavRoute.Add.route ||
        currentRoute == NavRoute.Profile.route
        // If you want to show the bar on Items too, you can add:
        // || currentRoute?.startsWith("items/") == true

    Scaffold(
        bottomBar = { if (showBottomBar) KeeprBottomBar(navController, navController.currentDestination) }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) NavRoute.Collections.route else ROUTE_SIGNUP
        ) {
            // ---------- AUTH ----------
            composable(ROUTE_SIGNUP) {
                com.example.keepr.ui.screens.auth.SignUpScreen(
                    vm = authVm,
                    onGoToSignIn = {
                        navController.navigate(ROUTE_SIGNIN) { launchSingleTop = true }
                    },
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

            // ---------- MAIN ----------
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
                // If your ProfileScreen expects 'padding' too, use:
                // ProfileScreen(paddingValues, onLogout = { ... })
                ProfileScreen(
                    onLogout = {
                        scope.launch { session.clear() }
                        navController.navigate(ROUTE_SIGNIN) {
                            popUpTo(0) { inclusive = true } // clear entire back stack
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = NavRoute.Items.route,
                arguments = listOf(navArgument("collectionId") { type = NavType.LongType })
            ) { entry ->
                val cid = entry.arguments!!.getLong("collectionId")
                ItemsScreen(
                    padding = paddingValues,
                    collectionId = cid,
                    onBack = { navController.popBackStack() },
                    navController = navController // <-- lets ItemsScreen navigate to Add
                )
            }
        }
    }

    // tiny logger to verify navigation
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, dest, _ ->
            android.util.Log.d("Nav", "Now at route: ${dest.route}")
        }
    }
}
