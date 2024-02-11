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
import kotlinx.coroutines.flow.map

internal class FilmsRepoImpl(
    private val filmsApi: FilmsApi,
    private val favouriteFilmsDao: FavouriteFilmsDao,
    private val filmsDao: FilmsDao,
) : FilmsRepo {

    override fun getPopularFilms(): Flow<DataState<List<Film>>> = channelFlow {
        val favouriteIds = favouriteFilmsDao.getAllIds()
        val cache = filmsDao.getFilms().map { it.toFilm(favourite = it.id in favouriteIds) }

        if (cache.isNotEmpty()) send(cache.success())
        else send(DataState.Loading())

        val fresh = filmsApi.getTopFilms(type = TYPE_POPULAR).mapToDataState(errorData = cache) { response ->
            response.films.mapNotNull {
                it.toFilm(favouriteIds = favouriteIds)
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

    override fun getFavouriteFilms(): Flow<List<Film>> = favouriteFilmsDao.getAll().map { films ->
        films.map { it.toFilm() }
    }

    override fun searchFilms(searchQuery: String): Flow<DataState<List<Film>>> = flow {
        emit(DataState.Loading())
        val favouriteIds = favouriteFilmsDao.getAllIds()
        val result = filmsApi.getFilmsByQuery(query = searchQuery).mapToDataState { response ->
            response.items.mapNotNull { it.toFilm(favouriteIds = favouriteIds) }
        }
        emit(result)
    }

    override fun getFilm(filmId: String): Flow<DataState<Film>> = flow {
        val favourite = favouriteFilmsDao.getFilm(filmId)?.toFilm()
        filmsDao.getFilm(id = filmId)?.let {
            emit(it.toFilm(favourite = favourite != null).success())
        } ?: run {
            favourite?.let {
                emit(it.success())
            } ?: run {
                emit(DataState.Loading())
            }
        }
        val result = filmsApi.getFilm(filmId = filmId).flatMapToDataState(errorData = favourite) {
            it.toFilm()?.success() ?: DataState.Error("Data loss", favourite)
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
            result.onSuccess {
                favouriteFilmsDao.saveFilm(it)
            }
            return result as DataState<Unit>
        }
    }

    companion object {
        private const val TYPE_POPULAR = "TOP_100_POPULAR_FILMS"
    }
}