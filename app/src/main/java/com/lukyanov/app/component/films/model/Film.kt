package com.lukyanov.app.component.films.model

class Film(
    val id: String,
    val name: String,
    val genres: List<String>,
    val year: Int?,
    val posterUrl: String?,
    val posterUrlPreview: String?,
)