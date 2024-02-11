package com.lukyanov.app.component.films.usecase

import com.lukyanov.app.component.films.FilmsRepo
import com.lukyanov.app.component.films.model.Film
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavouriteFilmsUseCase(
    private val filmsRepo: FilmsRepo,
) {
    operator fun invoke(searchQuery: String): Flow<List<Film>> =
        filmsRepo.getFavouriteFilms().map { films ->
            val cleanQuery = searchQuery.trim().lowercase()
            val (starts, other) = films.partition { it.name.lowercase().startsWith(cleanQuery) }
            val contains = other.filter { it.name.lowercase().contains(cleanQuery) }
            starts + contains
        }
}