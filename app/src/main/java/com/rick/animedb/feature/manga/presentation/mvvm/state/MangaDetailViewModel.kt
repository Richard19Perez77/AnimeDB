package com.rick.animedb.feature.manga.presentation.mvvm.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.animedb.feature.manga.domain.model.pickLanguage
import com.rick.animedb.feature.manga.domain.model.selectableLanguages
import com.rick.animedb.feature.manga.domain.usecase.GetMangaByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMangaById: GetMangaByIdUseCase,
) : ViewModel() {

    private val mangaId: String = checkNotNull(savedStateHandle[MangaIdArg])

    private val _uiState = MutableStateFlow<MangaDetailUiState>(MangaDetailUiState.Loading)
    val uiState: StateFlow<MangaDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    fun selectLanguage(code: String) {
        savedStateHandle[LanguageArg] = code
        val current = _uiState.value as? MangaDetailUiState.Success ?: return
        _uiState.value = current.copy(selectedLanguageCode = code)
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = MangaDetailUiState.Loading
            runCatching { getMangaById(mangaId) }
                .onSuccess { manga ->
                    val languages = manga.selectableLanguages()
                    val selected = savedStateHandle.get<String>(LanguageArg)
                        ?.takeIf { code -> languages.any { it.code.equals(code, ignoreCase = true) } }
                        ?: pickLanguage(languages.map { it.code }).orEmpty()
                    savedStateHandle[LanguageArg] = selected
                    _uiState.value = MangaDetailUiState.Success(
                        manga = manga,
                        languages = languages,
                        selectedLanguageCode = selected,
                    )
                }
                .onFailure { error ->
                    _uiState.value = MangaDetailUiState.Error(error.toMangaError())
                }
        }
    }

    companion object {
        const val MangaIdArg = "mangaId"
        private const val LanguageArg = "language"
    }
}
