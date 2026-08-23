package com.rick.animedb.feature.characters.presentation.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CharactersRoute(
    viewModel: CharactersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CharactersScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
    )
}
