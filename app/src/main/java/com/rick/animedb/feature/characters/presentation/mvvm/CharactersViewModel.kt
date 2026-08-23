package com.rick.animedb.feature.characters.presentation.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.animedb.feature.characters.domain.usecase.GetRandomCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getRandomCharacters: GetRandomCharactersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharactersUiState>(CharactersUiState.Loading)
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = CharactersUiState.Loading
            runCatching { getRandomCharacters() }
                .onSuccess { characters ->
                    _uiState.value = CharactersUiState.Success(characters)
                }
                .onFailure { error ->
                    _uiState.value = CharactersUiState.Error(error.toCharactersError())
                }
        }
    }
}

private fun Throwable.toCharactersError(): CharactersError = when {
    this is HttpException && code() == 429 -> CharactersError.RateLimit
    this is HttpException && code() in 500..599 -> CharactersError.Unavailable
    this is IOException -> CharactersError.Network
    else -> CharactersError.Unknown
}
