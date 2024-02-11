package com.lukyanov.app.features.film_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukyanov.app.common.ui.toUiTextOrGenericError
import com.lukyanov.app.component.films.FilmsRepo
import com.lukyanov.app.features.film_details.model.FilmDetailsNavEvent
import com.lukyanov.app.features.film_details.model.FilmDetailsUiState
import com.lukyanov.app.features.film_details.model.toFilmDetailsContent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class FilmDetailsViewModel(
    private val filmId: String,
    private val filmsRepo: FilmsRepo,
) : ViewModel() {

    private var filmObservation: Job? = null

    private val _uiState = MutableStateFlow<FilmDetailsUiState>(FilmDetailsUiState.Content())
    val uiState: StateFlow<FilmDetailsUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<FilmDetailsNavEvent>()
    val navEvent: Flow<FilmDetailsNavEvent> = _navEvent.receiveAsFlow()

    init {
        loadFilm(filmId = filmId)
    }

    private fun loadFilm(filmId: String) {
        filmObservation?.cancel()
        filmObservation = viewModelScope.launch {
            filmsRepo.getFilm(filmId = filmId).cancellable().collectLatest { result ->
                result.on(
                    loading = {
                        _uiState.update { FilmDetailsUiState.Loading }
                    },
                    success = { film ->
                        _uiState.update { film.toFilmDetailsContent() }
                    },
                    error = { error ->
                        _uiState.update { FilmDetailsUiState.Error(error.toUiTextOrGenericError()) }
                    }
                )
            }
        }
    }

    fun onReloadClick() {
        loadFilm(filmId = filmId)
    }

    fun onExitClick() = viewModelScope.launch {
        _navEvent.send(FilmDetailsNavEvent.Exit)
    }
}