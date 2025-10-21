package com.example.keepr.ui.navigation

sealed class NavRoute(val route: String, val label: String) {
    data object Collections : NavRoute("collections", "Collections")
    data object Wishlist   : NavRoute("wishlist",   "Wishlist")
    data object Stats      : NavRoute("stats",      "Stats")
    data object Settings   : NavRoute("settings",   "Settings")

    companion object {
        val bottomDestinations = listOf(Collections, Wishlist, Stats, Settings)
    }
}
