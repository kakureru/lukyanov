package com.lukyanov.app.component.films.model

class Film(
    val id: String,
    val name: String,
    val description: String?,
    val genres: List<String>,
    val countries: List<String>,
    val year: Int?,
    val posterUrl: String?,
    val posterUrlPreview: String?,
    val favourite: Boolean,
)