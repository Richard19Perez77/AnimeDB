package com.rick.animedb.feature.characters.presentation.mvvm

import com.rick.animedb.feature.characters.domain.model.AnimeCharacter

sealed interface CharactersUiState {
    data object Loading : CharactersUiState
    data class Success(val characters: List<AnimeCharacter>) : CharactersUiState
    data class Error(val reason: CharactersError) : CharactersUiState
}

enum class CharactersError {
    RateLimit,
    Unavailable,
    Network,
    Unknown,
}
