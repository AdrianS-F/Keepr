package com.example.keepr.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
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

    // Rebuild nav-graf når login-status endrer seg, så startDest fungerer.
    androidx.compose.runtime.key(userId) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination

        // Bottom bar kun på "main"-ruter
        val mainRoutes = remember {
            setOf(
                NavRoute.Collections.route,
                NavRoute.Add.route,
                NavRoute.Profile.route,
                NavRoute.Items.route
            )
        }
        val showBottomBar = when (val route = currentDestination?.route) {
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
                    // Din SignUpScreen skal kalle vm.signUp { ... } og deretter route videre
                    com.example.keepr.ui.screens.auth.SignUpScreen(
                        vm = authVm,
                        onGoToSignIn = {
                            navController.navigate(ROUTE_SIGNIN) { launchSingleTop = true }
                        },
                        onSignedIn = {
                            // Når signup ok -> til hoved-appen
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
                    ProfileScreen(
                        paddingValues,
                        // Valgfritt: legg til logout her, f.eks. knapp som:
                        // onLogout = {
                        //     CoroutineScope(Dispatchers.IO).launch { session.clear() }
                        //     navController.navigate(ROUTE_SIGNIN) {
                        //         popUpTo(0) { inclusive = true }
                        //         launchSingleTop = true
                        //     }
                        // }
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
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
