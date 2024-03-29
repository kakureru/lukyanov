package com.lukyanov.app.component.films.data.network.model

import kotlinx.serialization.Serializable

@Serializable
class GetFilmsResponse(
    val items: List<FilmDto>
)