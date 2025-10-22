package com.example.keepr.ui.navigation

sealed class NavRoute(val route: String, val label: String) {
    data object Collections : NavRoute("collections", "Collections")
    data object add   : NavRoute("add",   "add")
    data object profile      : NavRoute("profile",      "profile")


    companion object {
        val bottomDestinations = listOf(Collections, add, profile, )
    }
}
