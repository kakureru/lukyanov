package com.lukyanov.app.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lukyanov.app.features.film_details.FilmDetailsScreen
import com.lukyanov.app.features.films.ui.FilmsScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RootNavHost(
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = RootRoute.Films.route) {
        composable(route = RootRoute.Films.route) {
            FilmsScreen(
                viewModel = koinViewModel(),
                goToFilmDetails = { navController.navigate(RootRoute.FilmDetails.route + "/$it") }
            )
        }

        with(RootRoute.FilmDetails) {
            composable(
                route = "$route/{$ARG_FILM_ID}",
                arguments = listOf(
                    navArgument(ARG_FILM_ID) {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) { entry ->
                FilmDetailsScreen(
                    viewModel = koinViewModel { parametersOf(entry.arguments?.getString(ARG_FILM_ID)) },
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    }
}