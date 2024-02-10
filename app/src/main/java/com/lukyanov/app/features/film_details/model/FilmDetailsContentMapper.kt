package com.lukyanov.app.features.film_details.model

import com.lukyanov.app.component.films.model.Film

internal fun Film.toFilmDetailsContent() = FilmDetailsUiState.Content(
    posterUrl = posterUrl,
    title = name,
    description = description ?: "",
    genres = genres.joinToString(","),
    countries = countries.joinToString(","),
    favourite = favourite,
)