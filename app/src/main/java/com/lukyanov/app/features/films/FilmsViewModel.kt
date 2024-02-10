package com.lukyanov.app.features.films

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukyanov.app.R
import com.lukyanov.app.common.ui.UiText
import com.lukyanov.app.common.ui.toUiTextOrUnknownError
import com.lukyanov.app.component.films.FilmsRepo
import com.lukyanov.app.features.films.model.FilmFilter
import com.lukyanov.app.features.films.model.FilmFilterItem
import com.lukyanov.app.features.films.model.FilmListState
import com.lukyanov.app.features.films.model.FilmsNavEvent
import com.lukyanov.app.features.films.model.FilmsUiState
import com.lukyanov.app.features.films.model.TopBarState
import com.lukyanov.app.features.films.model.toFilmItemModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class FilmsViewModel(
    private val filmsRepo: FilmsRepo,
) : ViewModel() {

    private val initState = FilmsUiState(
        topBarState = TopBarState.Title(text = UiText.Resource(R.string.popular)),
        filters = listOf(
            FilmFilterItem(filter = FilmFilter.POPULAR, selected = true),
            FilmFilterItem(filter = FilmFilter.FAVOURITES, selected = false),
        )
    )

    private val _uiState = MutableStateFlow(initState)
    val uiState: StateFlow<FilmsUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<FilmsNavEvent>()
    val navEvent: Flow<FilmsNavEvent> = _navEvent.receiveAsFlow()

    init {
        loadFilms()
    }

    fun onFilmClick(filmId: String) = viewModelScope.launch {
        _navEvent.send(FilmsNavEvent.ToFilmDetails(filmId = filmId))
    }

    fun onFilmLongClick(filmId: String) = viewModelScope.launch {

    }

    fun onSearchClick() {
        _uiState.update {
            it.copy(topBarState = TopBarState.Search())
        }
    }

    fun onFilterClick(filter: FilmFilter) {

    }

    private fun loadFilms() = viewModelScope.launch {
        filmsRepo.getPopularFilms().collectLatest { result ->
            result.on(
                loading = {
                    _uiState.update { it.copy(filmsListState = FilmListState.Loading) }
                },
                success = { films ->
                    val filmsListState = FilmListState.Content(
                        films = films.map { it.toFilmItemModel() }
                    )
                    _uiState.update { it.copy(filmsListState = filmsListState) }
                },
                error = { msg ->
                    _uiState.update {
                        it.copy(filmsListState = FilmListState.Error(msg = msg.toUiTextOrUnknownError()))
                    }
                },
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(topBarState = TopBarState.Search(query = query))
        }
    }

    fun onStopSearchClick() {
        _uiState.update {
            it.copy(topBarState = TopBarState.Title(UiText.Resource(R.string.popular))) // FIXME
        }
    }

    fun onReloadClick() {
        loadFilms()
    }
}