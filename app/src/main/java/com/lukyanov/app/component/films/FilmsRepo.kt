package com.lukyanov.app.component.films

import com.lukyanov.app.common.util.DataState
import com.lukyanov.app.component.films.model.Film
import kotlinx.coroutines.flow.Flow

interface FilmsRepo {
    fun getPopularFilms(): Flow<DataState<List<Film>>>
    fun getFavouriteFilms(): Flow<List<Film>>
    fun searchFilms(searchQuery: String): Flow<DataState<List<Film>>>
    fun getFilm(filmId: String): Flow<DataState<Film>>
    suspend fun toggleFavourite(filmId: String): DataState<Unit>
}