package com.rick.animedb.feature.manga.presentation.mvvm

import com.rick.animedb.feature.manga.domain.model.Manga

sealed interface MangaUiState {
    data object Loading : MangaUiState
    data class Success(val manga: List<Manga>) : MangaUiState
    data class Error(val reason: MangaError) : MangaUiState
}

enum class MangaError {
    RateLimit,
    Unavailable,
    Network,
    Unknown,
}
