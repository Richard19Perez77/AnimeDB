package com.rick.animedb.feature.manga.presentation.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MangaRoute(
    viewModel: MangaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MangaScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
    )
}
