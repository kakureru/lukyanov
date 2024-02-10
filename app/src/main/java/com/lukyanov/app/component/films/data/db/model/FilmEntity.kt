package com.lukyanov.app.component.films.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lukyanov.app.component.films.data.db.converter.ListConverter
import com.lukyanov.app.component.films.data.network.model.FilmDto
import com.lukyanov.app.component.films.model.Film

@Entity(tableName = "films")
@TypeConverters(ListConverter::class)
data class FilmEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val name: String,
    val year: Int?,
    val posterUrlPreview: String?,
    val countries: List<String>,
    val genres: List<String>,
)

fun FilmDto.toFilmEntity(): FilmEntity? {
    return FilmEntity(
        id = id?.toString() ?: return null,
        name = name ?: return null,
        year = year,
        posterUrlPreview = posterUrlPreview,
        genres = genres?.map { it.genre } ?: emptyList(),
        countries = countries?.map { it.country } ?: emptyList(),
    )
}

fun Film.toFilmEntity(): FilmEntity {
    return FilmEntity(
        id = id,
        name = name,
        year = year,
        posterUrlPreview = posterUrlPreview,
        genres = genres,
        countries = countries,
    )
}

fun FilmEntity.toFilm(favourite: Boolean = false) = Film(
    id = id,
    name = name,
    posterUrl = null,
    posterUrlPreview = posterUrlPreview,
    description = null,
    year = year,
    countries = countries,
    genres = genres,
    favourite = favourite,
)