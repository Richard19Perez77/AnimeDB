package com.rick.animedb.feature.manga.presentation.mvvm.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rick.animedb.feature.manga.presentation.mvvm.screen.MangaDetailScreen
import com.rick.animedb.feature.manga.presentation.mvvm.state.MangaDetailViewModel

@Composable
fun MangaDetailRoute(
    onBack: () -> Unit,
    viewModel: MangaDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MangaDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onLanguageSelected = viewModel::selectLanguage,
    )
}
