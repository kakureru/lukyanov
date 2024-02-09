package com.lukyanov.app.features.films.navigation

import androidx.compose.runtime.Composable
import com.lukyanov.app.features.films.ui.FilmsScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FilmsRoot() {
    FilmsScreen(
        viewModel = koinViewModel(),
    )
}