package com.lukyanov.app.component.films.data.network.model

import kotlinx.serialization.Serializable

@Serializable
class GetFilmsResponse<T>(
    val films: List<T>
)