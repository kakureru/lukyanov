package com.lukyanov.app.app.ui

sealed class RootRoute(val route: String) {
    data object Films : RootRoute(route = "films")
    data object FilmDetails : RootRoute(route = "film_details") {
        const val ARG_FILM_ID = "ARG_FILM_ID"
    }
}