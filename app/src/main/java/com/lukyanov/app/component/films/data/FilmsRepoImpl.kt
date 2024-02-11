package com.lukyanov.app.component.films.data

import com.lukyanov.app.common.util.DataState
import com.lukyanov.app.common.util.request_result.flatMapToDataState
import com.lukyanov.app.common.util.request_result.mapToDataState
import com.lukyanov.app.common.util.success
import com.lukyanov.app.component.films.FilmsRepo
import com.lukyanov.app.component.films.data.db.dao.FavouriteFilmsDao
import com.lukyanov.app.component.films.data.db.dao.FilmsDao
import com.lukyanov.app.component.films.data.db.model.toFilm
import com.lukyanov.app.component.films.data.db.model.toFilmEntity
import com.lukyanov.app.component.films.data.network.FilmsApi
import com.lukyanov.app.component.films.data.network.model.toFilm
import com.lukyanov.app.component.films.model.Film
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

internal class FilmsRepoImpl(
    private val filmsApi: FilmsApi,
    private val favouriteFilmsDao: FavouriteFilmsDao,
    private val filmsDao: FilmsDao,
) : FilmsRepo {

    override fun getPopularFilms(): Flow<DataState<List<Film>>> = channelFlow {
        val cache = filmsDao.getFilms()
        val favouriteIds = favouriteFilmsDao.getAllIds()

        if (cache.isNotEmpty()) {
            val mappedData = cache.map { it.toFilm(favourite = it.id in favouriteIds) }
            send(mappedData.success())
        }
        else send(DataState.Loading())

        val fresh = filmsApi.getTopFilms(type = TYPE_POPULAR).mapToDataState { response ->
            response.films.mapNotNull {
                it.toFilm(favourite = it.id?.toString() in favouriteIds)
            }
        }
        send(fresh)

        fresh.onSuccess { films ->
            filmsDao.refreshFilms(films.map { it.toFilmEntity() })

            favouriteFilmsDao.getAllIdsFlow().collectLatest { newFavouriteIds ->
                val newData = filmsDao.getFilms().map {
                    it.toFilm(favourite = it.id in newFavouriteIds)
                }
                send(newData.success())
            }
        }
    }

    override fun searchFilms(searchQuery: String): Flow<DataState<List<Film>>> = flow {
        emit(DataState.Loading())
        val favouriteIds = favouriteFilmsDao.getAllIds()
        val result = filmsApi.getFilmsByQuery(query = searchQuery).mapToDataState { response ->
            response.items.mapNotNull { it.toFilm(favourite = it.id?.toString() in favouriteIds) }
        }
        emit(result)
    }

    override fun getFilm(filmId: String): Flow<DataState<Film>> = flow {
        emit(DataState.Loading())
        val result = filmsApi.getFilm(filmId = filmId).flatMapToDataState {
            it.toFilm()?.success() ?: DataState.Error("Data loss")
        }
        emit(result)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun toggleFavourite(filmId: String): DataState<Unit> {
        favouriteFilmsDao.getFilm(filmId = filmId)?.let {
            favouriteFilmsDao.deleteFilm(it)
            return Unit.success()
        } ?: run {
            val result = filmsApi.getFilm(filmId = filmId).flatMapToDataState { filmDto ->
                filmDto.toFilmEntity()?.success() ?: DataState.Error("Data loss")
            }
            return when (result) {
                is DataState.Success -> {
                    favouriteFilmsDao.saveFilm(result.data)
                    Unit.success()
                }
                else -> result as DataState<Unit>
            }
        }
    }

    companion object {
        private const val TYPE_POPULAR = "TOP_100_POPULAR_FILMS"
    }
}