package com.rick.animedb.feature.manga.presentation.mvvm

import com.rick.animedb.feature.manga.domain.model.Manga
import retrofit2.HttpException
import java.io.IOException

sealed interface MangaUiState {
    data object Loading : MangaUiState
    data class Success(val manga: List<Manga>) : MangaUiState
    data class Error(val reason: MangaError) : MangaUiState
}

sealed interface MangaDetailUiState {
    data object Loading : MangaDetailUiState
    data class Success(val manga: Manga) : MangaDetailUiState
    data class Error(val reason: MangaError) : MangaDetailUiState
}

enum class MangaError {
    RateLimit,
    Unavailable,
    Network,
    Unknown,
}

fun Throwable.toMangaError(): MangaError = when (this) {
    is HttpException if code() == 429 -> MangaError.RateLimit
    is HttpException if code() in 500..599 -> MangaError.Unavailable
    is HttpException if code() == 403 -> MangaError.Unavailable
    is IOException -> MangaError.Network
    else -> MangaError.Unknown
}
