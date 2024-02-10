package com.lukyanov.app.features.film_details.di

import com.lukyanov.app.features.film_details.FilmDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val filmDetailsFeatureModule = module {
    viewModel { params ->
        FilmDetailsViewModel(
            filmId = params.get(),
            filmsRepo = get(),
        )
    }
}