package com.lukyanov.app.features.films.model

import com.lukyanov.app.component.films.model.Film
import com.lukyanov.app.features.films.ui.FilmItemModel


internal fun Film.toFilmItemModel() = FilmItemModel(
    id = id,
    name = name,
    imageUrl = posterUrlPreview ?: "",
    description = buildString {
        genres.firstOrNull()?.let { append("${it.capitalize()} ") }
        append("($year)")
    },
    favourite = favourite,
)