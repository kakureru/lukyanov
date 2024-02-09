package com.lukyanov.app.component.films.data.network

import com.lukyanov.app.common.util.request_result.GenericRequestResult
import com.lukyanov.app.component.films.data.network.model.FilmDto
import com.lukyanov.app.component.films.data.network.model.GetFilmsResponse
import retrofit2.http.GET

internal interface FilmsApi {

    @GET("v2.2/films/top")
    suspend fun getTopFilms(): GenericRequestResult<GetFilmsResponse<FilmDto>>
}