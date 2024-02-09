package com.lukyanov.app.features.film.di

import com.lukyanov.app.features.film.FilmDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val filmDetailsFeatureModule = module {
    viewModel {
        FilmDetailsViewModel(

        )
    }
}