package com.example.keepr.ui.navigation

sealed class NavRoute(val route: String, val label: String) {
    data object Collections : NavRoute("collections", "Collections")
    data object Add   : NavRoute("add",   "add")
    data object Profile      : NavRoute("profile",      "profile")

    //Route for Items (når vi trykker på en collection) :)
    data object Items      : NavRoute("items/{collectionId}", "Items") {
        fun makeRoute(collectionId: Long) = "items/$collectionId"
    }


    companion object {
        val bottomDestinations = listOf(Collections, Add, Profile )
    }
}
