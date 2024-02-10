package com.lukyanov.app.features.films.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lukyanov.app.common.ui.CollectFlowSafelyLoosingProof
import com.lukyanov.app.common.ui.FullScreenLoader
import com.lukyanov.app.common.ui.GenericError
import com.lukyanov.app.common.ui.SearchTextField
import com.lukyanov.app.features.films.FilmsViewModel
import com.lukyanov.app.features.films.model.FilmFilter
import com.lukyanov.app.features.films.model.FilmFilterItem
import com.lukyanov.app.features.films.model.FilmListState
import com.lukyanov.app.features.films.model.FilmsNavEvent
import com.lukyanov.app.features.films.model.TopBarState

@Composable
internal fun FilmsScreen(
    viewModel: FilmsViewModel,
    goToFilmDetails: (filmId: String) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CollectFlowSafelyLoosingProof(flow = viewModel.navEvent) { navEvent ->
        when (navEvent) {
            is FilmsNavEvent.ToFilmDetails -> goToFilmDetails(navEvent.filmId)
        }
    }

    Scaffold(
        topBar = {
            FilmsTopBar(
                state = { state.topBarState },
                onSearchClick = viewModel::onSearchClick,
                onStopSearchClick = viewModel::onStopSearchClick,
                onSearchQueryChange = viewModel::onSearchQueryChange
            )
        }
    ) { paddingValues ->
        when (val filmsState = state.filmsListState) {
            FilmListState.Loading -> FullScreenLoader()

            is FilmListState.Error -> {
                GenericError(onButtonClick = viewModel::onReloadClick)
            }

            is FilmListState.Content -> {
                Box {
                    LazyColumn(
                        modifier = Modifier.padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                    ) {
                        items(items = filmsState.films, key = { item -> item.id }) {
                            FilmItem(
                                model = it,
                                onClick = { viewModel.onFilmClick(it.id) },
                                onLongClick = { viewModel.onFilmLongClick(it.id) },
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }

                    FilmFilterRow(
                        filters = { state.filters },
                        onFilterClick = viewModel::onFilterClick,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilmsTopBar(
    state: () -> TopBarState,
    onSearchClick: () -> Unit,
    onStopSearchClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
) {
    val topAppBarColors = TopAppBarDefaults.topAppBarColors(
        navigationIconContentColor = MaterialTheme.colorScheme.primary,
        actionIconContentColor = MaterialTheme.colorScheme.primary,
    )
    when (val stateSnap = state()) {
        is TopBarState.Search -> {
            TopAppBar(
                title = {
                    SearchTextField(
                        query = stateSnap.query,
                        onSearchQueryChange = onSearchQueryChange,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onStopSearchClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = topAppBarColors,
            )
        }

        is TopBarState.Title -> {
            TopAppBar(
                title = {
                    Text(
                        text = stateSnap.text.stringValue(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 25.sp,
                    )
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                        )
                    }
                },
                colors = topAppBarColors,
            )
        }
    }
}

@Composable
private fun FilmFilterRow(
    filters: () -> List<FilmFilterItem>,
    onFilterClick: (FilmFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        for (item in filters()) {
            FilterButton(
                text = item.filter.text.stringValue(),
                onClick = { onFilterClick(item.filter) },
                selected = item.selected,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun FilterButton(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors =
        if (selected) ButtonDefaults.buttonColors()
        else ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
        )
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 45.dp),
        colors = colors
    ) {
        Text(text = text)
    }
}