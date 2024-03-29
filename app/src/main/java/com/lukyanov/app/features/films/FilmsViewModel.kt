@file:OptIn(FlowPreview::class)

package com.lukyanov.app.features.films

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukyanov.app.R
import com.lukyanov.app.common.ui.UiText
import com.lukyanov.app.common.ui.toUiTextOrGenericError
import com.lukyanov.app.component.films.FilmsRepo
import com.lukyanov.app.component.films.model.Film
import com.lukyanov.app.component.films.usecase.GetFavouriteFilmsUseCase
import com.lukyanov.app.component.films.usecase.GetPopularFilmsUseCase
import com.lukyanov.app.features.films.model.FilmFilter
import com.lukyanov.app.features.films.model.FilmFilterItem
import com.lukyanov.app.features.films.model.FilmListState
import com.lukyanov.app.features.films.model.FilmsNavEvent
import com.lukyanov.app.features.films.model.FilmsUiEffect
import com.lukyanov.app.features.films.model.FilmsUiState
import com.lukyanov.app.features.films.model.TopBarState
import com.lukyanov.app.features.films.model.toFilmItemModel
import com.lukyanov.app.features.films.model.toFilmsListContent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class FilmsViewModel(
    private val filmsRepo: FilmsRepo,
    private val getPopularFilmsUseCase: GetPopularFilmsUseCase,
    private val getFavouriteFilmsUseCase: GetFavouriteFilmsUseCase,
) : ViewModel() {

    private var popularObservation: Job? = null
    private var favouriteObservation: Job? = null

    private val _searchQuery: MutableStateFlow<String?> = MutableStateFlow(null)

    private val initState = FilmsUiState(
        topBarState = TopBarState.Title(text = FilmFilter.POPULAR.text),
        filters = listOf(
            FilmFilterItem(filter = FilmFilter.POPULAR, selected = true),
            FilmFilterItem(filter = FilmFilter.FAVOURITES, selected = false),
        )
    )

    private val _uiState = MutableStateFlow(initState)
    val uiState: StateFlow<FilmsUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<FilmsUiEffect>()
    val uiEffect: Flow<FilmsUiEffect> = _uiEffect.receiveAsFlow()

    private val _navEvent = Channel<FilmsNavEvent>()
    val navEvent: Flow<FilmsNavEvent> = _navEvent.receiveAsFlow()

    init {
        loadPopularFilms()
        subscribeToSearchQueryChanges()
    }

    fun onFilmClick(filmId: String) = viewModelScope.launch {
        _navEvent.send(FilmsNavEvent.ToFilmDetails(filmId = filmId))
    }

    fun onFilmLongClick(filmId: String) = viewModelScope.launch {
        filmsRepo.toggleFavourite(filmId = filmId).onError { msg, _ ->
            _uiEffect.send(FilmsUiEffect.Error(msg.toUiTextOrGenericError()))
        }
    }

    fun onSearchClick() {
        _uiState.update {
            it.copy(topBarState = TopBarState.Search())
        }
    }

    fun onFilterClick(filter: FilmFilter) {
        _uiState.update {  state ->
            val newFilters = state.filters.map {
                if (it.filter == filter) it.copy(selected = true)
                else it.copy(selected = false)
            }
            val newBarState =
                if (state.topBarState is TopBarState.Title)
                    TopBarState.Title(text = filter.text)
                else state.topBarState

            state.copy(filters = newFilters, topBarState = newBarState)
        }
        val query = _searchQuery.value ?: ""
        when (filter) {
            FilmFilter.POPULAR -> loadPopularFilms(searchQuery = query)
            FilmFilter.FAVOURITES -> loadFavouriteFilms(searchQuery = query)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(topBarState = TopBarState.Search(query = query))
        }
        viewModelScope.launch {
            _searchQuery.update { query }
        }
    }

    fun onExitSearchClick() {
        _uiState.update { state ->
            val newBarState = uiState.value.filters
                .firstOrNull { it.selected }
                ?.filter?.text
                ?.let { TopBarState.Title(text = it) }
                ?: TopBarState.Title(FilmFilter.POPULAR.text)
            state.copy(topBarState = newBarState)
        }
        viewModelScope.launch {
            _searchQuery.update { "" }
        }
    }

    fun onReloadClick() {
        loadPopularFilms()
    }

    private fun loadPopularFilms(searchQuery: String = "") {
        favouriteObservation?.cancel()
        popularObservation?.cancel()
        popularObservation = viewModelScope.launch {
            getPopularFilmsUseCase(searchQuery = searchQuery)
                .cancellable()
                .collectLatest { result ->
                    result.on(
                        loading = {
                            _uiState.update { it.copy(filmsListState = FilmListState.Loading) }
                        },
                        success = { films ->
                            _uiState.update { it.copy(filmsListState = films.toFilmsListContent()) }
                        },
                        error = { msg, data ->
                            if (data != null) {
                                _uiState.update { it.copy(filmsListState = data.toFilmsListContent()) }
                                _uiEffect.send(FilmsUiEffect.Error(msg.toUiTextOrGenericError()))
                            } else
                                _uiState.update {
                                    it.copy(filmsListState = FilmListState.Error(msg = msg.toUiTextOrGenericError()))
                                }
                        },
                    )
                }
        }
    }

    private fun loadFavouriteFilms(searchQuery: String = "") {
        popularObservation?.cancel()
        favouriteObservation?.cancel()
        favouriteObservation = viewModelScope.launch {
            getFavouriteFilmsUseCase(searchQuery = searchQuery)
                .cancellable()
                .collectLatest { films ->
                    _uiState.update { it.copy(filmsListState = films.toFilmsListContent()) }
                }
        }
    }

    private fun performSearch(query: String) {
        when (uiState.value.filters.firstOrNull { it.selected }?.filter) {
            FilmFilter.POPULAR -> loadPopularFilms(searchQuery = query)
            FilmFilter.FAVOURITES -> loadFavouriteFilms(searchQuery = query)
            null -> Unit
        }
    }

    private fun subscribeToSearchQueryChanges() {
        _searchQuery
            .filterNotNull()
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { performSearch(query = it) }
            .launchIn(viewModelScope)
    }
}