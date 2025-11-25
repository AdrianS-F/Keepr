package com.example.keepr.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.example.keepr.ui.screens.AddScreen
import com.example.keepr.ui.screens.SignInScreen
import com.example.keepr.ui.screens.SignUpScreen
import com.example.keepr.ui.screens.CollectionsScreen
import com.example.keepr.ui.screens.ItemsScreen
import com.example.keepr.ui.screens.ProfileScreen
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


    val userId by session.loggedInUserId.collectAsState(initial = null)
    val isLoggedIn = userId != null


    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar =
        currentRoute?.startsWith("collections") == true ||
                currentRoute?.startsWith("add") == true ||
                currentRoute?.startsWith("profile") == true ||
                currentRoute?.startsWith("items/") == true



    val appSnackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = appSnackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor   = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionColor    = MaterialTheme.colorScheme.primary
                )
            }
        },

        bottomBar = { if (showBottomBar) { KeeprBottomBar(navController) } }

    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) NavRoute.Collections.route else ROUTE_SIGNUP
        ) {

            composable(ROUTE_SIGNUP) {
                SignUpScreen(
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
                SignInScreen(
                    vm = authVm,
                    onGoToSignUp = {
                        navController.navigate(ROUTE_SIGNUP) { launchSingleTop = true }
                    },
                    onSignedIn = {
                        navController.navigate(NavRoute.Collections.route) {
                            popUpTo(ROUTE_SIGNIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }


            composable(NavRoute.Collections.route) {
                CollectionsScreen(
                    padding = paddingValues,
                    onOpen = { collectionId ->
                        navController.navigate(NavRoute.Items.makeRoute(collectionId))
                    },
                    snackbarHostState = appSnackbarHostState

                )
            }

            composable(
                route = "add?collectionId={collectionId}",
                arguments = listOf(
                    navArgument("collectionId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { entry ->
                val rawId = entry.arguments?.getLong("collectionId") ?: -1L
                val initialCollectionId = if (rawId == -1L) null else rawId

                AddScreen(
                    padding = paddingValues,
                    initialCollectionId = initialCollectionId,
                    onSaved = { cid ->
                        navController.popBackStack()

                        navController.navigate(NavRoute.Items.makeRoute(cid)) {
                            launchSingleTop = true
                        }
                    }
                )
            }




            composable(NavRoute.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        scope.launch { session.clear() }
                        navController.navigate(ROUTE_SIGNIN) {
                            popUpTo(0) { inclusive = true }
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