package com.lukyanov.app.features.film_details.model

import com.lukyanov.app.common.ui.UiText

internal sealed interface FilmDetailsUiState {
    data object Loading : FilmDetailsUiState
    class Error(val msg: UiText) : FilmDetailsUiState
    data class Content(
        val posterUrl: String? = null,
        val title: String = "",
        val description: String = "",
        val genres: String = "",
        val countries: String = "",
        val favourite: Boolean = false,
    ) : FilmDetailsUiState
}

internal sealed interface FilmDetailsNavEvent {
    data object Exit : FilmDetailsNavEvent
}