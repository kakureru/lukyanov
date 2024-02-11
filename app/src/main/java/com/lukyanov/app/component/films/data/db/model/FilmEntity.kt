package com.lukyanov.app.component.films.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lukyanov.app.component.films.data.db.converter.ListConverter
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