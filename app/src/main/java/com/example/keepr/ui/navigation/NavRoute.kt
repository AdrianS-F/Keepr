package com.example.keepr.ui.navigation

sealed class NavRoute(val route: String, val label: String) {
    data object Collections : NavRoute("collections", "Collections")
    data object Add   : NavRoute("add",   "add")
    data object Profile      : NavRoute("profile",      "profile")


    companion object {
        val bottomDestinations = listOf(Collections, Add, Profile )
    }
}
