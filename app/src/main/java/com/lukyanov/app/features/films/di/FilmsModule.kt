package com.lukyanov.app.features.films.di

import com.lukyanov.app.features.films.FilmsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val filmsFeatureModule = module {
    viewModel {
        FilmsViewModel(
            filmsRepo = get(),
            getPopularFilmsUseCase = get(),
            getFavouriteFilmsUseCase = get(),
        )
    }
}