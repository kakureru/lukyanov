package com.lukyanov.app.component.films.data.network

import com.lukyanov.app.common.util.request_result.GenericRequestResult
import com.lukyanov.app.component.films.data.network.model.FilmDetailsDto
import com.lukyanov.app.component.films.data.network.model.FilmDto
import com.lukyanov.app.component.films.data.network.model.GetFilmsResponse
import com.lukyanov.app.component.films.data.network.model.GetPopularFilmsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface FilmsApi {

    @GET("v2.2/films/top")
    suspend fun getTopFilms(
        @Query("type") type: String,
    ): GenericRequestResult<GetPopularFilmsResponse>

    @GET("v2.2/films/{id}")
    suspend fun getFilm(
        @Path("id") filmId: String,
    ): GenericRequestResult<FilmDetailsDto>

    @GET("v2.2/films")
    suspend fun getFilmsByQuery(
        @Query("keyword") query: String
    ): GenericRequestResult<GetFilmsResponse>
}