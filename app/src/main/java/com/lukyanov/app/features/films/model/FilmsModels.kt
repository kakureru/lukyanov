package com.lukyanov.app.features.films.model

import com.lukyanov.app.R
import com.lukyanov.app.common.ui.UiText
import com.lukyanov.app.features.films.ui.FilmItemModel

internal data class FilmsUiState(
    val topBarState: TopBarState,
    val filmsListState: FilmListState = FilmListState.Content(),
    val filters: List<FilmFilterItem>,
)

internal data class FilmFilterItem(
    val filter: FilmFilter,
    val selected: Boolean,
)

internal enum class FilmFilter(val text: UiText) {
    POPULAR(text = UiText.Resource(R.string.popular)),
    FAVOURITES(text = UiText.Resource(R.string.favourites)),
}

internal sealed interface FilmListState {
    data object Loading : FilmListState
    data class Error(val msg: UiText) : FilmListState
    data class Content(
        val films: List<FilmItemModel> = emptyList(),
    ) : FilmListState
}

internal sealed interface TopBarState {
    data class Title(val text: UiText) : TopBarState
    data class Search(val query: String = "") : TopBarState
}

internal sealed interface FilmsUiEffect {
    class Error(val msg: UiText) : FilmsUiEffect
}

internal sealed interface FilmsNavEvent {
    class ToFilmDetails(val filmId: String) : FilmsNavEvent
}