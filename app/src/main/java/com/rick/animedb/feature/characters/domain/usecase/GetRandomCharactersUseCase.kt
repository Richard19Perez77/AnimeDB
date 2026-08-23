package com.rick.animedb.feature.characters.domain.usecase

import com.rick.animedb.feature.characters.domain.model.AnimeCharacter
import com.rick.animedb.feature.characters.domain.repository.CharacterRepository
import javax.inject.Inject

class GetRandomCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(count: Int = DefaultCount): List<AnimeCharacter> =
        repository.getRandomCharacters(count)

    private companion object {
        const val DefaultCount = 8
    }
}
