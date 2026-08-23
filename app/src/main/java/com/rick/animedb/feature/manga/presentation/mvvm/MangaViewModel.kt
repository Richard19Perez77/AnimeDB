package com.rick.animedb.feature.manga.presentation.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.animedb.feature.manga.domain.usecase.GetTopMangaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MangaViewModel @Inject constructor(
    private val getTopManga: GetTopMangaUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MangaUiState>(MangaUiState.Loading)
    val uiState: StateFlow<MangaUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = MangaUiState.Loading
            runCatching { getTopManga() }
                .onSuccess { manga ->
                    _uiState.value = MangaUiState.Success(manga)
                }
                .onFailure { error ->
                    _uiState.value = MangaUiState.Error(error.toMangaError())
                }
        }
    }
}

private fun Throwable.toMangaError(): MangaError = when {
    this is HttpException && code() == 429 -> MangaError.RateLimit
    this is HttpException && code() in 500..599 -> MangaError.Unavailable
    this is HttpException && code() == 403 -> MangaError.Unavailable
    this is IOException -> MangaError.Network
    else -> MangaError.Unknown
}
