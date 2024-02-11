package com.lukyanov.app.features.film_details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.lukyanov.app.R
import com.lukyanov.app.common.ui.BaseError
import com.lukyanov.app.common.ui.CollectFlowSafelyLoosingProof
import com.lukyanov.app.common.ui.FullScreenLoader
import com.lukyanov.app.features.film_details.model.FilmDetailsNavEvent
import com.lukyanov.app.features.film_details.model.FilmDetailsUiState

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun FilmDetailsScreen(
    viewModel: FilmDetailsViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CollectFlowSafelyLoosingProof(flow = viewModel.navEvent) {
        when (it) {
            FilmDetailsNavEvent.Exit -> onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Unit },
                colors = topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = viewModel::onExitClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val stateSnap = state) {

            FilmDetailsUiState.Loading -> {
                FullScreenLoader()
            }

            is FilmDetailsUiState.Error -> {
                BaseError(
                    text = stateSnap.msg.stringValue(),
                    buttonText = stringResource(id = R.string.action_repeat),
                    onButtonClick = viewModel::onReloadClick,
                )
            }

            is FilmDetailsUiState.Content -> {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .consumeWindowInsets(paddingValues)
                ) {
                    AsyncImage(
                        model = stateSnap.posterUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                    )

                    FilmInfo(
                        state = stateSnap,
                        modifier = Modifier.padding(vertical = 20.dp, horizontal = 30.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilmInfo(
    modifier: Modifier = Modifier,
    state: FilmDetailsUiState.Content,
) {
    Column(modifier = modifier) {
        Text(
            text = state.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = state.description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(stringResource(id = R.string.genres) + ": ")
                }
                append(state.genres)
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(stringResource(id = R.string.countries) + ": ")
                }
                append(state.countries)
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}