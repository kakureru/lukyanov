package com.lukyanov.app.component.films.data

import com.lukyanov.app.common.util.DataState
import com.lukyanov.app.common.util.request_result.flatMapToDataState
import com.lukyanov.app.common.util.request_result.mapToDataState
import com.lukyanov.app.common.util.success
import com.lukyanov.app.component.films.FilmsRepo
import com.lukyanov.app.component.films.model.Film
import com.lukyanov.app.component.films.data.network.FilmsApi
import com.lukyanov.app.component.films.data.network.model.toFilm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class FilmsRepoImpl(
    private val filmsApi: FilmsApi,
) : FilmsRepo {

    override fun getPopularFilms(): Flow<DataState<List<Film>>> = flow {
        emit(DataState.Loading())
        val films = filmsApi.getTopFilms().mapToDataState { response ->
            response.films.mapNotNull { it.toFilm() }
        }
        emit(films)
    }

    override fun getFilm(filmId: String): Flow<DataState<Film>> = flow {
        emit(DataState.Loading())
        val result = filmsApi.getFilm(filmId = filmId).flatMapToDataState {
            it.toFilm()?.success() ?: DataState.Error("Data loss")
        }
        emit(result)
    }
}