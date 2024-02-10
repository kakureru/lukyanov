package com.lukyanov.app.component.films.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lukyanov.app.component.films.data.db.converter.ListConverter
import com.lukyanov.app.component.films.data.network.model.FilmDetailsDto
import com.lukyanov.app.component.films.model.Film

@Entity(tableName = "favourite_films")
@TypeConverters(ListConverter::class)
data class FavouriteFilmEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val name: String,
    val year: Int?,
    val description: String?,
    val posterUrl: String?,
    val countries: List<String>,
    val genres: List<String>,
)

fun FilmDetailsDto.toFilmEntity(): FavouriteFilmEntity? {
    return FavouriteFilmEntity(
        id = id?.toString() ?: return null,
        name = name ?: return null,
        year = year,
        description = description,
        posterUrl = posterUrl,
        genres = genres?.map { it.genre } ?: emptyList(),
        countries = countries?.map { it.country } ?: emptyList(),
    )
}

fun FavouriteFilmEntity.toFilm() = Film(
    id = id,
    name = name,
    posterUrl = posterUrl,
    posterUrlPreview = null,
    description = description,
    year = year,
    countries = countries,
    genres = genres,
    favourite = true,
)