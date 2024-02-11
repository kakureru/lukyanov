package com.lukyanov.app.component.films.usecase

import com.lukyanov.app.component.films.FilmsRepo

class GetPopularFilmsUseCase(
    private val filmsRepo: FilmsRepo,
) {
    operator fun invoke(searchQuery: String) =
        if (searchQuery.isNotBlank())
            filmsRepo.searchFilms(searchQuery = searchQuery)
        else
            filmsRepo.getPopularFilms()
}