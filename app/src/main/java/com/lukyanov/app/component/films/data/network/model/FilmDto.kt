package com.lukyanov.app.component.films.data.network.model

import com.lukyanov.app.component.films.model.Film
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class FilmDto(
    @SerialName("filmId") val id: Int? = null,
    @SerialName("nameRu") val name: String? = null,
    val genres: List<GenreDto>? = null,
    val countries: List<CountryDto>? = null,
    val year: Int? = null,
    val posterUrl: String? = null,
    val posterUrlPreview: String? = null,
)

fun FilmDto.toFilm(favourite: Boolean = false): Film? {
    return Film(
        id = id?.toString() ?: return null,
        name = name ?: return null,
        description = null,
        genres = genres?.map { it.genre } ?: emptyList(),
        countries = countries?.map { it.country } ?: emptyList(),
        year = year,
        posterUrl = posterUrl,
        posterUrlPreview = posterUrlPreview,
        favourite = favourite,
    )
}