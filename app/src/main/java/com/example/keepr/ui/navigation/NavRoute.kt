package com.example.keepr.ui.navigation


sealed class NavRoute(val route: String, val label: String) {
    data object Collections : NavRoute(route = "collections", label = "Collections")
    data object Add : NavRoute(route = "add", label = "Add") {
        fun forCollection(collectionId: Long) = "add?collectionId=$collectionId"
    }

    data object Profile    : NavRoute(route = "profile",     label = "Profile")
    data object Items      : NavRoute(route = "items/{collectionId}", label = "Items") {
        fun makeRoute(collectionId: Long) = "items/$collectionId"
    }

    // Auth-ruter – label brukes ikke i bottom bar, men må oppgis
    data object SignUp     : NavRoute(route = "signup",  label = "Sign up")
    data object SignIn     : NavRoute(route = "signin",  label = "Sign in")

    companion object {
        // Bottom bar skal bare vise hovedruter, ikke auth
        val bottomDestinations: List<NavRoute> =
            listOf(Collections, Add, Profile)
    }
}
