package com.rick.animedb.feature.manga.presentation.mvvm.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.animedb.feature.manga.presentation.mvvm.screen.MangaScreen
import com.rick.animedb.feature.manga.presentation.mvvm.state.MangaViewModel

@Composable
fun MangaRoute(
    onMangaClick: (String) -> Unit,
    viewModel: MangaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MangaScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        onMangaClick = onMangaClick,
    )
}
